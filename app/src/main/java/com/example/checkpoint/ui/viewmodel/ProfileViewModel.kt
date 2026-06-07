package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.remote.igdb.IgdbClient
import com.example.checkpoint.data.remote.igdb.IgdbImageUrl.coverBig
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.repositories.GameLogRepository
import com.example.checkpoint.data.repositories.ReviewRepository
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

data class AchievementUiModel(
	val id: Int,
	val code: String,
	val name: String,
	val description: String?,
	val iconUrl: String?,
	val threshold: Int,
	val progress: Int,
	val unlockedAt: String?,
	val isPinned: Boolean = false
) {
	val isUnlocked: Boolean get() = progress >= threshold
	val progressFraction: Float get() = (progress.toFloat() / threshold).coerceIn(0f, 1f)
}

data class ProfileState(
	val user: UserEntity? = null,
	val reviews: List<ReviewEntity> = emptyList(),
	val achievements: List<AchievementUiModel> = emptyList(),
	val preferredGenres: List<String> = emptyList(),
	val userLists: List<GameListEntity> = emptyList(),
	val totalHours: Double = 0.0,
	val completedGamesCount: Int = 0,
	val carousels: List<LibraryListUiModel> = emptyList(),
	val isLoading: Boolean = true,
)

class ProfileViewModel(
	private val sessionManager: SessionManager,
	private val userDao: UserDao,
	private val reviewRepository: ReviewRepository,
	private val achievementRepository: AchievementRepository,
	private val genreDao: GenreDao,
	private val gameListRepository: GameListRepository,
	private val gameLogRepository: GameLogRepository,
	private val igdbClient: IgdbClient
) : ViewModel() {

	@OptIn(ExperimentalCoroutinesApi::class)
	val state: StateFlow<ProfileState> = sessionManager.sessionState.flatMapLatest { session ->
		if (session is SessionState.LoggedIn) {

			val userReviewGenreFlow = combine(
				userDao.getUserById(session.userId),
				reviewRepository.getReviewsByUser(session.userId),
				genreDao.getPreferredGenresForUser(session.userId)
			) { user, reviews, genres -> Triple(user, reviews, genres) }

			val achievementsFlow = combine(
				achievementRepository.getAllAchievements(),
				achievementRepository.getAchievementsForUser(session.userId)
			) { allAch, userAch -> Pair(allAch, userAch) }

			// Merges list loading, IGDB fetch for games, and logs
			val carouselsAndLogsFlow = combine(
				gameListRepository.getListsForUser(session.userId).map { rawLists ->
					val defaultLists = ensureDefaultLists(rawLists, session.userId)
					defaultLists.map { list ->
						val gameEntities = gameListRepository.getGamesInList(list.id).first()
						val igdbIds = gameEntities.map { it.igdbId }
						val games = if (igdbIds.isNotEmpty()) {
							igdbClient.getGamesByIds(igdbIds).map { dto ->
								Game(
									id = 0,
									igdbId = dto.id,
									name = dto.name ?: "Unknown",
									summary = dto.summary,
									coverUrl = dto.cover?.imageId?.let {
										coverBig(it)
									},
									genres = dto.genres?.mapNotNull { it.name } ?: emptyList(),
									platforms = dto.platforms?.mapNotNull { it.name }
										?: emptyList(),
									developer = dto.involvedCompanies?.firstOrNull { it.developer == true }?.company?.name,
									publisher = dto.involvedCompanies?.firstOrNull { it.publisher == true }?.company?.name,
									firstReleaseDate = dto.firstReleaseDate,
									totalRating = dto.totalRating,
									totalRatingCount = dto.totalRatingCount ?: 0
								)
							}
						} else {
							emptyList()
						}
						LibraryListUiModel(list, games)
					}
				},
				gameLogRepository.getLogsForUser(session.userId)
			) { carousels, logs -> Pair(carousels, logs) }

			combine(
				userReviewGenreFlow, achievementsFlow, carouselsAndLogsFlow
			) { (user, reviews, genres), (allAchievements, userProgress), (carousels, logs) ->

				val progressMap = userProgress.associateBy { it.achievementId }
				val uiAchievements = allAchievements.map { ach ->
					val prog = progressMap[ach.id]
					AchievementUiModel(
						id = ach.id,
						code = ach.code,
						name = ach.name,
						description = ach.description,
						iconUrl = ach.iconUrl,
						threshold = ach.threshold,
						progress = prog?.progress ?: 0,
						unlockedAt = prog?.unlockedAt,
						isPinned = prog?.isPinned ?: false
					)
				}

				val totalHours = logs.sumOf { it.hoursPlayed ?: 0.0 }
				val completedCount = logs.count { it.finishedAt != null }

				ProfileState(
					user = user,
					reviews = reviews,
					achievements = uiAchievements,
					preferredGenres = genres.map { it.name },
					userLists = carousels.map { it.listEntity },
					carousels = carousels,
					totalHours = totalHours,
					completedGamesCount = completedCount,
					isLoading = false
				)
			}
		} else {
			flowOf(ProfileState(isLoading = false))
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = ProfileState(isLoading = true)
	)

	private fun ensureDefaultLists(lists: List<GameListEntity>, userId: Int): List<GameListEntity> {
		val hasBacklog =
			lists.any { it.type == "BACKLOG" || it.name.equals("Backlog", ignoreCase = true) }
		val hasSaved =
			lists.any { it.type == "SAVED" || it.name.equals("Saved", ignoreCase = true) }

		val result = lists.toMutableList()

		if (!hasBacklog) result.add(
			GameListEntity(
				userId = userId, name = "Backlog", type = "BACKLOG"
			)
		)
		if (!hasSaved) result.add(GameListEntity(userId = userId, name = "Saved", type = "SAVED"))

		return result.sortedWith(compareBy<GameListEntity> {
			when (it.type) {
				"BACKLOG" -> 0
				"SAVED" -> 1
				else -> 2
			}
		}.thenBy { it.id })
	}
}