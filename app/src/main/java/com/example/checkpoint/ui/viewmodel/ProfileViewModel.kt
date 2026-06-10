package com.example.checkpoint.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.daos.GameDao
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.daos.UserPreferredGenreDao
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.database.entities.UserPreferredGenreEntity
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.repositories.UserRepository
import com.example.checkpoint.data.security.PasswordHasher
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

// ─── Shared UI models

data class AchievementUiModel(
	val id: Int,
	val code: String,
	val name: String,
	val description: String?,
	val iconUrl: String?,
	val threshold: Int,
	val progress: Int,
	val unlockedAt: String?,
	val isPinned: Boolean,
	val categoryId: Int
) {
	val isUnlocked: Boolean get() = progress >= threshold
	val progressFraction: Float get() = (progress.toFloat() / threshold).coerceIn(0f, 1f)
}

data class ProfileUiState(
	val user: UserEntity? = null,
	val isLoading: Boolean = false,
	val error: String? = null,
	val achievements: List<AchievementUiModel> = emptyList(),
	val preferredGenres: List<String> = emptyList(),
	val carousels: List<LibraryListUiModel> = emptyList(),
	val reviews: List<ReviewEntity> = emptyList(),
	val allAvailableGenres: List<String> = emptyList(),
	val igdbIdByGameId: Map<Int, Int> = emptyMap(),
	val gameNameByGameId: Map<Int, String> = emptyMap()
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
	private val userRepository: UserRepository,
	private val gameRepository: GameRepository,
	private val gameListRepository: GameListRepository,
	private val achievementRepository: AchievementRepository,
	private val reviewDao: ReviewDao,
	private val genreDao: GenreDao,
	private val userPreferredGenreDao: UserPreferredGenreDao,
	sessionManager: SessionManager,
	private val gameDao: GameDao,
) : ViewModel() {

	// Get User ID from Session
	private val loggedInUserId =
		sessionManager.sessionState.map { if (it is SessionState.LoggedIn) it.userId else null }
			.distinctUntilChanged()

	val state: StateFlow<ProfileUiState> = loggedInUserId.flatMapLatest { userId ->
		if (userId == null) {
			flowOf(ProfileUiState(isLoading = false))
		} else {
			val userFlow = userRepository.getUserById(userId)

			val achievementsFlow = achievementRepository.getAchievementsForUser(userId)
				.combine(achievementRepository.getAllAchievements()) { userAchs, allAchs ->
					userAchs.mapNotNull { ua ->
						val ach = allAchs.find { it.id == ua.achievementId }
						if (ach != null) {
							AchievementUiModel(
								id = ach.id,
								code = ach.code,
								name = ach.name,
								description = ach.description,
								iconUrl = ach.iconUrl,
								threshold = ach.threshold,
								progress = ua.progress,
								unlockedAt = ua.unlockedAt,
								isPinned = ua.isPinned,
								categoryId = ach.categoryId
							)
						} else null
					}
				}

			// Review flow + map gameId -> igdbId e gameId -> name for navigation and UI
			val reviewsFlow = reviewDao.getReviewsByUser(userId)
				.flatMapLatest { reviews ->
					flow {
						val info = resolveGameInfo(reviews.map { it.gameId }.distinct())
						emit(Triple(reviews, info.first, info.second))
					}
				}

			val preferredGenresFlow =
				genreDao.getPreferredGenresForUser(userId).map { list -> list.map { it.name } }

			val allGenresFlow =
				gameRepository.getAllGenresFromDb().map { list -> list.map { it.name } }

			// Carousel with real IGDB fetch by name and cover
			val carouselsFlow = gameListRepository.getListsForUser(userId)
				.flatMapLatest { lists ->
					if (lists.isEmpty()) {
						flowOf(emptyList())
					} else {
						val carouselFlows = lists.map { listEntity ->
							gameListRepository.getGamesInList(listEntity.id)
								.flatMapLatest { entities ->
									flow {
										val games =
											fetchGamesFromEntities(entities.map { it.igdbId })
										emit(LibraryListUiModel(listEntity, games))
									}
								}
						}
						combine(carouselFlows) { array: Array<LibraryListUiModel> -> array.toList() }
					}
				}

			combine(
				combine(userFlow, achievementsFlow, ::Pair),
				combine(reviewsFlow, preferredGenresFlow, ::Pair),
				combine(allGenresFlow, carouselsFlow, ::Pair)
			) { (user, achs), (revTriple, prefG), (allG, carousels) ->
				val (revs, igdbIdByGameId, gameNameByGameId) = revTriple
				ProfileUiState(
					user = user,
					isLoading = false,
					achievements = achs,
					reviews = revs,
					igdbIdByGameId = igdbIdByGameId,
					gameNameByGameId = gameNameByGameId,
					preferredGenres = prefG,
					allAvailableGenres = allG,
					carousels = carousels
				)
			}
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = ProfileUiState(isLoading = true)
	)

	/**
	 * For each gameId (Room) resolves igdbId and game name in parallel.
	 * Returns Pair(igdbIdByGameId, gameNameByGameId).
	 */
	private suspend fun resolveGameInfo(gameIds: List<Int>): Pair<Map<Int, Int>, Map<Int, String>> =
		coroutineScope {
			gameIds.map { gameId ->
				async {
					val entity = gameDao.getById(gameId)
					val igdbId = entity?.igdbId
					val name = igdbId?.let { gameRepository.fetchGameDetails(it)?.name }
					Triple(gameId, igdbId, name)
				}
			}.awaitAll().let { results ->
				val igdbIdMap = results.mapNotNull { (gameId, igdbId, _) ->
					igdbId?.let { gameId to it }
				}.toMap()
				val nameMap = results.mapNotNull { (gameId, _, name) ->
					name?.let { gameId to it }
				}.toMap()
				Pair(igdbIdMap, nameMap)
			}
		}

	/** Fetch the IGDB details in parallel for a list of igdbIds */
	private suspend fun fetchGamesFromEntities(igdbIds: List<Int>): List<Game> =
		coroutineScope {
			igdbIds.map { igdbId ->
				async {
					gameRepository.fetchGameDetails(igdbId) ?: Game(
						id = 0,
						igdbId = igdbId,
						name = "Game #$igdbId",
						summary = null,
						coverUrl = null,
						genres = emptyList(),
						platforms = emptyList(),
						developer = null,
						publisher = null,
						firstReleaseDate = null,
						totalRating = null,
						totalRatingCount = null
					)
				}
			}.awaitAll()
		}

	suspend fun updatePassword(current: String, new: String): Result<Unit> {
		val currentUser = state.value.user ?: return Result.failure(Exception("User not logged in"))

		val isCorrect = if (currentUser.passwordHash.contains(":")) {
			val parts = currentUser.passwordHash.split(":")
			PasswordHasher.verifyPassword(current, parts[0], parts[1])
		} else {
			currentUser.passwordHash == current
		}

		if (!isCorrect) {
			return Result.failure(Exception("Your current password is incorrect"))
		}

		return try {
			val salt = PasswordHasher.generateSalt()
			val hash = PasswordHasher.hashPassword(new, salt)
			val combinedHash = "$salt:$hash"
			userRepository.upsertUser(currentUser.copy(passwordHash = combinedHash))
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	// ── Biography

	fun updateBiography(newBio: String) {
		val user = state.value.user ?: return
		viewModelScope.launch {
			userRepository.upsertUser(user.copy(bio = newBio))
		}
	}

	// ── Profile visibility

	fun setProfilePublic(isPublic: Boolean) {
		val user = state.value.user ?: return
		viewModelScope.launch {
			userRepository.upsertUser(user.copy(publicProfile = isPublic))
		}
	}

	// ── Favorite genres

	fun updatePreferredGenres(genreNames: List<String>) {
		val user = state.value.user ?: return
		viewModelScope.launch {
			userPreferredGenreDao.deleteAllForUser(user.id)
			val allGenres = gameRepository.getAllGenresFromDb().first()
			genreNames.forEach { name ->
				allGenres.find { it.name == name }?.let { genre ->
					userPreferredGenreDao.upsert(
						UserPreferredGenreEntity(userId = user.id, genreId = genre.id)
					)
				}
			}
		}
	}

	fun updateAvatar(context: Context, uri: Uri) {
		val user = state.value.user ?: return
		viewModelScope.launch {
			val path = saveAvatarLocal(context, uri, user.id)
			if (path != null) {
				userRepository.upsertUser(user.copy(avatarUrl = path))
			}
		}
	}

	private fun saveAvatarLocal(context: Context, uri: Uri, userId: Int): String? {
		return try {
			val avatarDir = File(context.filesDir, "avatars").also { it.mkdirs() }
			val destFile = File(avatarDir, "user_$userId.jpg")
			context.contentResolver.openInputStream(uri)?.use { input ->
				FileOutputStream(destFile).use { output -> input.copyTo(output) }
			}
			destFile.absolutePath
		} catch (_: Exception) {
			null
		}
	}
}