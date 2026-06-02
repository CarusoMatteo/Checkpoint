package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreState(
	val popularGames: List<Game> = emptyList(),
	val recentReleases: List<Game> = emptyList(),
	val becauseYouPlayed: List<Game> = emptyList(),
	val bestOnPlatform: List<Game> = emptyList(),
	val sinceYouLikeGenre: List<Game> = emptyList(),
	val comingSoonGames: List<Game> = emptyList(),
	val searchResults: List<Game> = emptyList(),
	val searchQuery: String = "", // test
	val isLoadingPopular: Boolean = false,
	val isLoadingRecent: Boolean = false,
	val isLoadingComingSoon: Boolean = false,
	val isLoadingRecommendations: Boolean = false,
	val isSearching: Boolean = false,
	val error: String? = null
)

data class ExploreActions(
	val onSearchQueryChange: (String) -> Unit,
	val onSearchSubmit: () -> Unit,
	val onClearSearch: () -> Unit,
	val onRefresh: () -> Unit
)

@OptIn(FlowPreview::class)
class ExploreViewModel(
	private val gameRepository: GameRepository
) : ViewModel() {

	private val _state = MutableStateFlow(ExploreState())
	val state: StateFlow<ExploreState> = _state.asStateFlow()

	// FIX: Sincronizziamo il flow interno con il valore iniziale dello stato
	private val _searchQuery = MutableStateFlow(_state.value.searchQuery)

	val actions = ExploreActions(
		onSearchQueryChange = { query ->
			_state.update { it.copy(searchQuery = query) }
			_searchQuery.value = query
		},
		onSearchSubmit = {
			val query = _state.value.searchQuery
			if (query.isNotBlank()) search(query)
		},
		onClearSearch = {
			_state.update { it.copy(searchQuery = "", searchResults = emptyList()) }
			_searchQuery.value = ""
		},
		onRefresh = { loadInitialData() }
	)

	init {
		loadInitialData()

		// Se c'è già una query di default (es. "final fantasy"), esegue subito la ricerca iniziale
		if (_searchQuery.value.isNotBlank()) {
			search(_searchQuery.value)
		}

		// Ricerca con debounce per i cambi di testo successivi
		viewModelScope.launch {
			_searchQuery
				.debounce(400)
				.distinctUntilChanged()
				.collect { query ->
					if (query.isBlank()) {
						_state.update { it.copy(searchResults = emptyList(), isSearching = false) }
					} else {
						search(query)
					}
				}
		}
	}

	private fun loadInitialData() {
		loadPopularGames()
		loadRecentReleases()
		loadRecommendations()
		loadComingSoonGames()
	}

	private fun loadPopularGames() {
		viewModelScope.launch {
			_state.update { it.copy(isLoadingPopular = true, error = null) }
			val games = gameRepository.getPopularGames()
			_state.update { it.copy(popularGames = games, isLoadingPopular = false) }
		}
	}

	private fun loadRecentReleases() {
		viewModelScope.launch {
			_state.update { it.copy(isLoadingRecent = true) }
			val games = gameRepository.getRecentReleases()
			_state.update { it.copy(recentReleases = games, isLoadingRecent = false) }
		}
	}

	private fun loadComingSoonGames() {
		viewModelScope.launch {
			_state.update { it.copy(isLoadingComingSoon = true) }
			val games = gameRepository.getComingSoonGames()
			_state.update { it.copy(comingSoonGames = games, isLoadingComingSoon = false) }
		}
	}

	private fun loadRecommendations() {
		viewModelScope.launch {
			_state.update { it.copy(isLoadingRecommendations = true) }

			// TODO: Sostituisci questi metodi fittizi con le reali implementazioni del tuo repository
			val becausePlayed = gameRepository.getPopularGames().shuffled().take(5)
			val bestPlatform = gameRepository.getRecentReleases().shuffled().take(5)
			val sinceGenre = gameRepository.getPopularGames().take(5)

			_state.update {
				it.copy(
					becauseYouPlayed = becausePlayed,
					bestOnPlatform = bestPlatform,
					sinceYouLikeGenre = sinceGenre,
					isLoadingRecommendations = false
				)
			}
		}
	}

	private fun search(query: String) {
		viewModelScope.launch {
			_state.update { it.copy(isSearching = true) }
			val results = gameRepository.searchGames(query)
			_state.update { it.copy(searchResults = results, isSearching = false) }
		}
	}
}