package com.example.checkpoint.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.repositories.UserRepository
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.daos.UserPreferredGenreDao
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.database.entities.UserPreferredGenreEntity
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

// ─── Profile state

data class ProfileState(
	val user: UserEntity? = null,
	val achievements: List<AchievementUiModel> = emptyList(),
	val reviews: List<ReviewEntity> = emptyList(),
	val carousels: List<LibraryListUiModel> = emptyList(),
	val preferredGenres: List<String> = emptyList(),
	val allAvailableGenres: List<String> = emptyList(),
	val isLoading: Boolean = true,
	val error: String? = null
)

// ─── ViewModel

class ProfileViewModel(
	private val sessionManager: SessionManager,
	private val userRepository: UserRepository,
	private val gameRepository: GameRepository,
	private val gameListRepository: GameListRepository,
	private val achievementRepository: AchievementRepository,
	private val genreDao: GenreDao,
	private val userPreferredGenreDao: UserPreferredGenreDao,
	private val reviewDao: ReviewDao
) : ViewModel() {

	/**
	 * Use flatMapLatest on the sessionState: whenever the session changes
	 * (Loading -> LoggedIn, LoggedIn -> LoggedOut, etc.) the internal flow is
	 * Automatically deleted and recreated.
	 *
	 */
	@OptIn(ExperimentalCoroutinesApi::class)
	val state: StateFlow<ProfileState> = sessionManager.sessionState.flatMapLatest { session ->
		when (session) {
			is SessionState.Loading -> flowOf(ProfileState(isLoading = true))
			is SessionState.LoggedOut -> flowOf(ProfileState(isLoading = false))
			is SessionState.LoggedIn -> buildProfileFlow(session.userId)
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = ProfileState(isLoading = true)
	)

	/**
	 * Combine all DB responsive flows for a given userId.
	 */
	@OptIn(ExperimentalCoroutinesApi::class)
	private fun buildProfileFlow(userId: Int) = combine(
		// User + favorite genres + all genres
		combine(
			userRepository.getUserById(userId),
			genreDao.getPreferredGenresForUser(userId),
			gameRepository.getAllGenresFromDb()
		) { user, preferredGenres, allGenres ->
			Triple(user, preferredGenres.map { it.name }, allGenres.map { it.name })
		},

		// Achievement  + progress
		combine(
			achievementRepository.getAllAchievements(),
			achievementRepository.getAchievementsForUser(userId)
		) { allAch, userAch ->
			val userMap = userAch.associateBy { it.achievementId }
			allAch.map { ach ->
				val ua = userMap[ach.id]
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
			}
		},

		// Reviews
		reviewDao.getReviewsByUser(userId),

		// Collections
		gameListRepository.getListsForUser(userId).flatMapLatest { lists ->
			if (lists.isEmpty()) {
				flowOf(emptyList())
			} else {
				combine(
					lists.map { list ->
						gameListRepository.getGamesInList(list.id).map { gameEntities ->
							val games = gameEntities.mapNotNull { entity ->
								gameRepository.fetchGameDetails(entity.igdbId) ?: Game(
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
							LibraryListUiModel(listEntity = list, games = games)
						}
					}) { it.toList() }
			}
		}

	) { (user, preferredGenres, allGenres), achievements, reviews, carousels ->
		ProfileState(
			user = user,
			achievements = achievements,
			reviews = reviews,
			carousels = carousels,
			preferredGenres = preferredGenres,
			allAvailableGenres = allGenres,
			isLoading = false,
			error = null
		)
	}

	// ── Avatar

	fun updateAvatar(context: Context, uri: Uri) {
		val user = state.value.user ?: return
		viewModelScope.launch {
			val path = saveAvatarToInternalStorage(context, uri, user.id)
			if (path != null) {
				userRepository.upsertUser(user.copy(avatarUrl = path))
			}
		}
	}

	private fun saveAvatarToInternalStorage(context: Context, uri: Uri, userId: Int): String? {
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
			val nameToEntity = allGenres.associateBy { it.name }
			genreNames.forEach { name ->
				nameToEntity[name]?.let { genre ->
					userPreferredGenreDao.upsert(
						UserPreferredGenreEntity(userId = user.id, genreId = genre.id)
					)
				}
			}
		}
	}
}