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
	val searchResults: List<Game> = emptyList(),
	val searchQuery: String = "",
	val isLoadingPopular: Boolean = false,
	val isLoadingRecent: Boolean = false,
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

	private val _searchQuery = MutableStateFlow("")

	val actions = ExploreActions(onSearchQueryChange = { query ->
		_state.update { it.copy(searchQuery = query) }
		_searchQuery.value = query
	}, onSearchSubmit = {
		val query = _state.value.searchQuery
		if (query.isNotBlank()) search(query)
	}, onClearSearch = {
		_state.update { it.copy(searchQuery = "", searchResults = emptyList()) }
		_searchQuery.value = ""
	}, onRefresh = { loadInitialData() })

	init {
		loadInitialData()

		// Ricerca con debounce: aspetta 400ms dopo l'ultimo input prima di cercare
		viewModelScope.launch {
			_searchQuery.debounce(400).distinctUntilChanged().collect { query ->
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

	private fun search(query: String) {
		viewModelScope.launch {
			_state.update { it.copy(isSearching = true) }
			val results = gameRepository.searchGames(query)
			_state.update { it.copy(searchResults = results, isSearching = false) }
		}
	}
}