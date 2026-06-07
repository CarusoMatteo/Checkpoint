package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.database.entities.GameLogEntity
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.repositories.CompletionType
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.repositories.GameLogRepository
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.repositories.ReviewRepository
import com.example.checkpoint.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class GameScreenState(
	val game: Game? = null,
	val similarGames: List<Game> = emptyList(),
	val franchiseGames: List<Game> = emptyList(),
	val userLog: GameLogEntity? = null,
	val userReview: ReviewEntity? = null,
	val reviews: List<ReviewEntity> = emptyList(),
	val reviewUsers: Map<Int, UserEntity> = emptyMap(),
	val averageRating: Double? = null,
	val reviewCount: Int = 0,
	val isLoading: Boolean = true,
	val isSaved: Boolean = false,
	val userLists: List<GameListEntity> = emptyList(),
	val listsContainingGame: Set<Int> = emptySet(),
	val error: String? = null,
)

data class GameScreenActions(
	val onSaveGame: () -> Unit,
	val onRemoveGame: () -> Unit,
	val onLogGame: (
		rating: Int?,
		hoursPlayed: Double?,
		completionType: CompletionType?,
		startedAt: LocalDate?,
		finishedAt: LocalDate?,
	) -> Unit,
	val onWriteReview: (rating: Float, body: String, completion: CompletionType) -> Unit,
	val onDeleteReview: () -> Unit,
	val onAddGameToBacklog: () -> Unit,
	val onSynchronizeLists: (listIds: List<Int>) -> Unit,
	val onCreateNewList: (name: String) -> Unit,
)

class GameScreenViewModel(
	private val igdbId: Int,
	private val userId: Int,
	private val gameRepository: GameRepository,
	private val gameLogRepository: GameLogRepository,
	private val reviewRepository: ReviewRepository,
	private val gameListRepository: GameListRepository,
	private val userRepository: UserRepository
) : ViewModel() {

	private val _state = MutableStateFlow(GameScreenState())
	val state: StateFlow<GameScreenState> = _state

	val actions = GameScreenActions(
		onSaveGame = { saveGame() },
		onRemoveGame = { removeGame() },
		onLogGame = { r, h, c, s, f -> logGame(r, h, c, s, f) },
		onWriteReview = { r, b, c -> writeReview(r, b, c) },
		onDeleteReview = { deleteReview() },
		onAddGameToBacklog = { addGameToBacklog() },
		onSynchronizeLists = { listIds -> synchronizeGameLists(listIds) },
		onCreateNewList = { name -> createList(name) })

	init {
		loadGame()
		observeUserLists()
	}

	private fun loadGame() {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true, error = null) }

			val game = gameRepository.fetchGameDetails(igdbId)
			if (game == null) {
				_state.update {
					it.copy(isLoading = false, error = "Unable to load game details")
				}
				return@launch
			}

			val isSaved = gameRepository.savedIgdbIds.first().contains(igdbId)
			val similar = gameRepository.getSimilarGames(igdbId)
			val franchise = gameRepository.getFranchiseGames(igdbId)

			_state.update {
				it.copy(
					game = game,
					similarGames = similar,
					franchiseGames = franchise,
					isLoading = false,
					isSaved = isSaved
				)
			}

			val localEntity = gameRepository.getLocalEntityByIgdbId(igdbId)
			val gameId = localEntity?.id ?: game.id
			if (gameId != 0) observeUserData(gameId)
		}
	}

	private fun observeUserData(gameId: Int) {
		viewModelScope.launch {
			gameLogRepository.getLogForGame(userId, gameId)
				.collect { log -> _state.update { it.copy(userLog = log) } }
		}
		viewModelScope.launch {
			reviewRepository.getReviewForGame(userId, gameId)
				.collect { review -> _state.update { it.copy(userReview = review) } }
		}
		viewModelScope.launch {
			reviewRepository.getAverageRating(gameId)
				.collect { avg -> _state.update { it.copy(averageRating = avg) } }
		}
		viewModelScope.launch {
			reviewRepository.getReviewCount(gameId)
				.collect { count -> _state.update { it.copy(reviewCount = count) } }
		}

		viewModelScope.launch {
			gameListRepository.getListsContainingGame(gameId)
				.collect { listIds -> _state.update { it.copy(listsContainingGame = listIds.toSet()) } }
		}

		// I retrieve reviews and users
		viewModelScope.launch {
			reviewRepository.getReviewsForGame(gameId).collect { reviews ->

				_state.update { it.copy(reviews = reviews) }

				// I extract the unique IDs and ask the database who they are
				val authorIds = reviews.map { it.userId }.distinct()
				if (authorIds.isNotEmpty()) {
					val users = userRepository.getUsersByIds(authorIds)
					val usersMap = users.associateBy { it.id }
					_state.update { it.copy(reviewUsers = usersMap) }
				}
			}
		}
	}

	private fun observeUserLists() {
		viewModelScope.launch {
			gameListRepository.getListsForUser(userId).collect { lists ->
				_state.update { it.copy(userLists = lists) }
			}
		}
	}

	private fun addGameToBacklog() {
		val backlogList = _state.value.userLists.firstOrNull {
			it.type == "BACKLOG" || it.name.equals("Backlog", ignoreCase = true)
		}
		if (backlogList != null) {
			synchronizeGameLists(listOf(backlogList.id))
		} else {
			saveGame()
		}
	}

	private fun synchronizeGameLists(selectedListIds: List<Int>) {
		viewModelScope.launch {
			val localEntity = gameRepository.getLocalEntityByIgdbId(igdbId)
			val gameId = localEntity?.id ?: gameRepository.saveGame(igdbId).id

			val currentLists = _state.value.listsContainingGame
			val toAdd = selectedListIds.filter { it !in currentLists }
			val toRemove = currentLists.filter { it !in selectedListIds }

			toAdd.forEach { listId ->
				gameListRepository.addGameToList(listId, gameId)
			}
			toRemove.forEach { listId ->
				gameListRepository.removeGameFromList(listId, gameId)
			}

			_state.update { it.copy(isSaved = true) }
			observeUserData(gameId)
		}
	}

	private fun createList(name: String) {
		viewModelScope.launch {
			gameListRepository.createList(userId = userId, name = name)
		}
	}

	private fun saveGame() {
		viewModelScope.launch {
			val entity = gameRepository.saveGame(igdbId)
			_state.update { it.copy(isSaved = true) }
			observeUserData(entity.id)
		}
	}

	private fun removeGame() {
		viewModelScope.launch {
			val entity = gameRepository.getLocalEntityByIgdbId(igdbId) ?: return@launch
			gameRepository.deleteGame(entity)
			_state.update {
				it.copy(
					isSaved = false,
					userLog = null,
					userReview = null,
					reviews = emptyList(),
					listsContainingGame = emptySet()
				)
			}
		}
	}

	private fun logGame(
		rating: Int?,
		hoursPlayed: Double?,
		completionType: CompletionType?,
		startedAt: LocalDate?,
		finishedAt: LocalDate?,
	) {
		val gameId = _state.value.game?.id ?: return
		val existingId = _state.value.userLog?.id ?: 0
		viewModelScope.launch {
			gameLogRepository.upsertLog(
				userId = userId,
				gameId = gameId,
				rating = rating,
				hoursPlayed = hoursPlayed,
				completionType = completionType,
				startedAt = startedAt,
				finishedAt = finishedAt,
				existingId = existingId,
			)
		}
	}

	private fun writeReview(rating: Float, body: String, completion: CompletionType) {
		val gameId = _state.value.game?.id ?: return
		val existingId = _state.value.userReview?.id ?: 0
		viewModelScope.launch {
			reviewRepository.upsertReview(
				userId = userId,
				gameId = gameId,
				rating = rating,
				body = body,
				completion = completion,
				existingId = existingId,
			)
		}
	}

	private fun deleteReview() {
		val review = _state.value.userReview ?: return
		viewModelScope.launch { reviewRepository.deleteReview(review) }
	}
}