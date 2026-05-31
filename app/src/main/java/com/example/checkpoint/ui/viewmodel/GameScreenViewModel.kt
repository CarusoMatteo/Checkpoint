package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.entities.GameLogEntity
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.repositories.CompletionType
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameLogRepository
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.repositories.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class GameScreenState(
	val game: Game? = null,
	val userLog: GameLogEntity? = null,
	val userReview: ReviewEntity? = null,
	val reviews: List<ReviewEntity> = emptyList(),
	val averageRating: Double? = null,
	val reviewCount: Int = 0,
	val isLoading: Boolean = true,
	val isSaved: Boolean = false,
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
	val onWriteReview: (rating: Int, body: String, containsSpoilers: Boolean) -> Unit,
	val onDeleteReview: () -> Unit,
)

class GameScreenViewModel(
	private val igdbId: Int,
	private val userId: Int,
	private val gameRepository: GameRepository,
	private val gameLogRepository: GameLogRepository,
	private val reviewRepository: ReviewRepository,
) : ViewModel() {

	private val _state = MutableStateFlow(GameScreenState())
	val state: StateFlow<GameScreenState> = _state

	val actions = GameScreenActions(
		onSaveGame = { saveGame() },
		onRemoveGame = { removeGame() },
		onLogGame = { r, h, c, s, f -> logGame(r, h, c, s, f) },
		onWriteReview = { r, b, sp -> writeReview(r, b, sp) },
		onDeleteReview = { deleteReview() },
	)

	init {
		loadGame()
	}

	private fun loadGame() {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true, error = null) }

			val game = gameRepository.fetchGameDetails(igdbId)
			if (game == null) {
				_state.update {
					it.copy(
						isLoading = false, error = "Impossibile caricare il gioco"
					)
				}
				return@launch
			}

			val isSaved = gameRepository.savedIgdbIds.first().contains(igdbId)
			_state.update { it.copy(game = game, isLoading = false, isSaved = isSaved) }

			// Se il gioco è già salvato localmente, osserva log e recensioni
			if (isSaved && game.id != 0) observeUserData(game.id)
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
			reviewRepository.getReviewsForGame(gameId)
				.collect { reviews -> _state.update { it.copy(reviews = reviews) } }
		}
		viewModelScope.launch {
			reviewRepository.getAverageRating(gameId)
				.collect { avg -> _state.update { it.copy(averageRating = avg) } }
		}
		viewModelScope.launch {
			reviewRepository.getReviewCount(gameId)
				.collect { count -> _state.update { it.copy(reviewCount = count) } }
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
					isSaved = false, userLog = null, userReview = null, reviews = emptyList()
				)
			}
		}
	}

	// ── Log

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

	private fun writeReview(rating: Int, body: String, containsSpoilers: Boolean) {
		val gameId = _state.value.game?.id ?: return
		val existingId = _state.value.userReview?.id ?: 0
		viewModelScope.launch {
			reviewRepository.upsertReview(
				userId = userId,
				gameId = gameId,
				rating = rating,
				body = body,
				containsSpoilers = containsSpoilers,
				existingId = existingId,
			)
		}
	}

	private fun deleteReview() {
		val review = _state.value.userReview ?: return
		viewModelScope.launch { reviewRepository.deleteReview(review) }
	}
}
