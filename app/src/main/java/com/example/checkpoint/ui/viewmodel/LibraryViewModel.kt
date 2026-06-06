package com.example.checkpoint.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.remote.igdb.IgdbClient
import com.example.checkpoint.data.remote.igdb.IgdbImageUrl
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Modello UI che la schermata si aspetta di ricevere
data class LibraryListUiModel(
	val listEntity: GameListEntity, val games: List<Game>
)

// Stato completo della libreria
data class LibraryState(
	val isLoading: Boolean = true,
	val carousels: List<LibraryListUiModel> = emptyList(),
	val errorMessage: String? = null
)

class LibraryViewModel(
	private val sessionManager: SessionManager,
	private val gameListRepository: GameListRepository,
	private val igdbClient: IgdbClient
) : ViewModel() {

	private val _state = MutableStateFlow(LibraryState())
	val state: StateFlow<LibraryState> = _state.asStateFlow()

	init {
		observeLibrary()
	}

	private fun observeLibrary() {
		viewModelScope.launch {
			sessionManager.sessionState.collectLatest { session ->
				if (session is SessionState.LoggedIn) {
					_state.value = LibraryState(isLoading = true)

					gameListRepository.getListsForUser(session.userId).collectLatest { lists ->
						val loadedCarousels = mutableListOf<LibraryListUiModel>()

						val sortedLists = lists.sortedWith(
							compareBy<GameListEntity> {
								when (it.type) {
									"BACKLOG" -> 0
									"SAVED" -> 1
									else -> 2 // Le CUSTOM vanno per ultime
								}
							}.thenBy { it.id } // A parità di type (es. CUSTOM), ordina per ID di creazione
						)

						for (list in sortedLists) {
							try {
								// Usiamo first() per evitare loop infiniti
								val gameEntities =
									gameListRepository.getGamesInList(list.id).first()
								val igdbIds = gameEntities.map { it.igdbId }

								val games = if (igdbIds.isNotEmpty()) {
									val igdbGames = igdbClient.getGamesByIds(igdbIds)
									igdbGames.map { dto ->
										Game(
											id = 0,
											igdbId = dto.id,
											name = dto.name ?: "Unknown",
											summary = dto.summary,
											coverUrl = dto.cover?.imageId?.let {
												IgdbImageUrl.coverBig(
													it
												)
											},
											genres = dto.genres?.mapNotNull { it.name }
												?: emptyList(),
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

								loadedCarousels.add(LibraryListUiModel(list, games))
							} catch (e: Exception) {
								Log.e(
									"LibraryViewModel",
									"Errore nel caricamento della lista ${list.name}",
									e
								)
							}
						}

						// Spegne il caricamento e aggiorna i caroselli
						_state.value = LibraryState(
							isLoading = false, carousels = loadedCarousels
						)
					}
				} else {
					_state.value = LibraryState(
						isLoading = false,
						errorMessage = "Devi effettuare l'accesso per vedere la libreria."
					)
				}
			}
		}
	}

	// ─────────────────────────────────────────────────────────────────────────
	// AZIONI UI CHIAMATE DALLA LIBRARYSCREEN
	// ─────────────────────────────────────────────────────────────────────────

	fun createCustomList(name: String) {
		viewModelScope.launch {
			val userId = (sessionManager.sessionState.value as? SessionState.LoggedIn)?.userId ?: 1
			gameListRepository.createList(userId, name, "CUSTOM", true)
		}
	}

	fun deleteCustomList(listId: Int) {
		viewModelScope.launch {
			val list = gameListRepository.getListById(listId).first()
			if (list != null) {
				// 1. Elimina dal Database
				gameListRepository.deleteList(list)

				// 2. Aggiorna la UI istantaneamente (rimuove il carosello)
				val updatedCarousels =
					_state.value.carousels.filterNot { it.listEntity.id == listId }
				_state.value = _state.value.copy(carousels = updatedCarousels)
			}
		}
	}

	fun removeGameFromListByIgdbId(listId: Int, igdbId: Int) {
		viewModelScope.launch {
			// 1. Cerca l'entità locale tramite l'igdbId
			val gameEntities = gameListRepository.getGamesInList(listId).first()
			val localGame = gameEntities.find { it.igdbId == igdbId }

			if (localGame != null) {
				// 2. Elimina dal Database usando il tuo metodo deleteByIds
				gameListRepository.removeGameFromList(listId, localGame.id)

				// 3. Aggiorna la UI istantaneamente (rimuove il gioco dal carosello specifico)
				val updatedCarousels = _state.value.carousels.map { carousel ->
					if (carousel.listEntity.id == listId) {
						carousel.copy(games = carousel.games.filterNot { it.igdbId == igdbId })
					} else {
						carousel
					}
				}
				_state.value = _state.value.copy(carousels = updatedCarousels)
			}
		}
	}
}