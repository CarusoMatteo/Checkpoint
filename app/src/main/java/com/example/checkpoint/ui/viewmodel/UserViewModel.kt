package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.daos.GameDao
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.repositories.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*

data class UserUiState(
	val user: UserEntity? = null,
	val isLoading: Boolean = true,
	val isPrivate: Boolean = false,
	val achievements: List<AchievementUiModel> = emptyList(),
	val preferredGenres: List<String> = emptyList(),
	val carousels: List<LibraryListUiModel> = emptyList(),
	val reviews: List<ReviewEntity> = emptyList(),
	val allAvailableGenres: List<String> = emptyList(),
	val igdbIdByGameId: Map<Int, Int> = emptyMap()
)

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModel(
	private val userId: Int,
	private val userRepository: UserRepository,
	private val gameRepository: GameRepository,
	private val gameListRepository: GameListRepository,
	private val achievementRepository: AchievementRepository,
	private val reviewDao: ReviewDao,
	private val genreDao: GenreDao,
	private val gameDao: GameDao,
) : ViewModel() {

	val state: StateFlow<UserUiState> = userRepository.getUserById(userId).flatMapLatest { user ->
		if (user == null) {
			flowOf(UserUiState(isLoading = false))
		} else if (!user.publicProfile) {
			flowOf(UserUiState(user = user, isLoading = false, isPrivate = true))
		} else {
			val achievementsFlow = achievementRepository.getAchievementsForUser(userId)
				.combine(achievementRepository.getAllAchievements()) { userAchs, allAchs ->
					userAchs.mapNotNull { ua ->
						val ach = allAchs.find { it.id == ua.achievementId }
						if (ach != null) AchievementUiModel(
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
						) else null
					}
				}

			// Review flow + map gameId -> igdbId for navigating to game
			val reviewsFlow = reviewDao.getReviewsByUser(userId).flatMapLatest { reviews ->
				flow {
					val igdbIdByGameId =
						resolveIgdbIds(reviews.map { it.gameId }.distinct())
					emit(Pair(reviews, igdbIdByGameId))
				}
			}

			val preferredGenresFlow =
				genreDao.getPreferredGenresForUser(userId).map { list -> list.map { it.name } }

			val allGenresFlow =
				gameRepository.getAllGenresFromDb().map { list -> list.map { it.name } }

			// Carousel
			val carouselsFlow =
				gameListRepository.getListsForUser(userId).flatMapLatest { lists ->
					if (lists.isEmpty()) flowOf(emptyList())
					else {
						val flows = lists.map { listEntity ->
							gameListRepository.getGamesInList(listEntity.id)
								.flatMapLatest { entities ->
									flow {
										val games =
											fetchGamesFromEntities(entities.map { it.igdbId })
										emit(LibraryListUiModel(listEntity, games))
									}
								}
						}
						combine(flows) { it.toList() }
					}
				}

			combine(
				combine(flowOf(user), achievementsFlow, ::Pair),
				combine(reviewsFlow, preferredGenresFlow, ::Pair),
				combine(allGenresFlow, carouselsFlow, ::Pair)
			) { (u, achs), (revPair, prefG), (allG, carousels) ->
				val (revs, igdbIdByGameId) = revPair
				UserUiState(
					user = u,
					isLoading = false,
					isPrivate = false,
					achievements = achs,
					reviews = revs,
					igdbIdByGameId = igdbIdByGameId,
					preferredGenres = prefG,
					allAvailableGenres = allG,
					carousels = carousels
				)
			}
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = UserUiState(isLoading = true)
	)

	/** Resolves a list of gameIds (Rooms) in a map gameId -> igdbId */
	private suspend fun resolveIgdbIds(gameIds: List<Int>): Map<Int, Int> = coroutineScope {
		gameIds.map { gameId ->
			async { gameId to (gameDao.getById(gameId)?.igdbId) }
		}.awaitAll().mapNotNull { (gameId, igdbId) -> igdbId?.let { gameId to it } }.toMap()
	}

	/** Fetch the IGDB details in parallel for a list of igdbIds */
	private suspend fun fetchGamesFromEntities(igdbIds: List<Int>): List<Game> = coroutineScope {
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
}