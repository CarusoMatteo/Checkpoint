package com.example.checkpoint.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.daos.UserPreferredGenreDao
import com.example.checkpoint.data.database.entities.GameListEntity
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
	val allAvailableGenres: List<String> = emptyList()
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
	private val sessionManager: SessionManager
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
								progress = ua?.progress ?: 0,
								unlockedAt = ua?.unlockedAt,
								isPinned = ua?.isPinned ?: false,
								categoryId = ach.categoryId
							)
						} else null
					}
				}

			val reviewsFlow = reviewDao.getReviewsByUser(userId)

			val preferredGenresFlow =
				genreDao.getPreferredGenresForUser(userId).map { list -> list.map { it.name } }

			val allGenresFlow =
				gameRepository.getAllGenresFromDb().map { list -> list.map { it.name } }

			val carouselsFlow = gameListRepository.getListsForUser(userId).flatMapLatest { lists ->
				if (lists.isEmpty()) {
					flowOf(emptyList<LibraryListUiModel>())
				} else {
					val carouselFlows = lists.map { listEntity ->
						gameListRepository.getGamesInList(listEntity.id).map { gameEntities ->
							val games = gameEntities.map { entity ->
								Game(
									id = entity.id,
									igdbId = entity.igdbId,
									name = "Game #${entity.igdbId}",
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
							LibraryListUiModel(listEntity, games)
						}
					}
					// combine flows into a single list
					combine(carouselFlows) { array: Array<LibraryListUiModel> -> array.toList() }
				}
			}
			combine(
				combine(userFlow, achievementsFlow, reviewsFlow, ::Triple),
				combine(preferredGenresFlow, allGenresFlow, carouselsFlow, ::Triple)
			) { (user, achs, revs), (prefG, allG, carousels) ->
				ProfileUiState(
					user = user,
					isLoading = false,
					achievements = achs,
					reviews = revs,
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