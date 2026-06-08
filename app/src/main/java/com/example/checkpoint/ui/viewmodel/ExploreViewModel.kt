package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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
	val searchQuery: String = "",
	val availableGenres: List<GenreEntity> = emptyList(),
	val availablePlatforms: List<PlatformEntity> = emptyList(),
	val selectedGenreIds: Set<Int> = emptySet(),
	val selectedPlatformIds: Set<Int> = emptySet(),

	val isLoadingPopular: Boolean = false,
	val isLoadingRecent: Boolean = false,
	val isLoadingComingSoon: Boolean = false,
	val isLoadingRecommendations: Boolean = false,
	val isSearching: Boolean = false,
	val error: String? = null
)

class ExploreViewModel(
	private val gameRepository: GameRepository
) : ViewModel() {

	private var searchJob: Job? = null

	private val _state = MutableStateFlow(ExploreState())
	val state: StateFlow<ExploreState> = _state.asStateFlow()

	init {
		loadInitialData()
		observeSearchTriggers()
	}

	private fun loadInitialData() {
		loadPopularGames()
		loadRecentReleases()
		loadRecommendations()
		loadComingSoonGames()
		loadGenresAndPlatforms()
	}

	private fun loadGenresAndPlatforms() {
		// 1. Remote Synchronization (IGDB -> Room)
		viewModelScope.launch {
			try {
				gameRepository.fetchGenresFromIgdb()
			} catch (e: Exception) {
				_state.update { it.copy(error = "Error loading genres from IGDB: ${e.message}") }
			}
		}

		viewModelScope.launch {
			try {
				gameRepository.fetchPlatformsFromIgdb()
			} catch (e: Exception) {
				_state.update { it.copy(error = "Error loading platforms from IGDB: ${e.message}") }
			}
		}

		viewModelScope.launch {
			gameRepository.getAllGenresFromDb().collect { genresList ->
				_state.update { it.copy(availableGenres = genresList) }
			}
		}

		viewModelScope.launch {
			gameRepository.getAllPlatformsFromDb().collect { platformsList ->
				_state.update { it.copy(availablePlatforms = platformsList) }
			}
		}
	}

	@OptIn(FlowPreview::class)
	private fun observeSearchTriggers() {
		// Observe Text, Genres, and Platforms simultaneously.
		// If any of the three changes, start the filtered search
		viewModelScope.launch {
			_state.map { Triple(it.searchQuery, it.selectedGenreIds, it.selectedPlatformIds) }
				.distinctUntilChanged()
				.debounce(400) // wait
				.collect { (query, genres, platforms) ->
					if (query.isBlank() && genres.isEmpty() && platforms.isEmpty()) {
						_state.update { it.copy(searchResults = emptyList(), isSearching = false) }
					} else {
						executeSearch(query, genres.toList(), platforms.toList())
					}
				}
		}
	}

	private fun executeSearch(query: String, genres: List<Int>, platforms: List<Int>) {
		searchJob?.cancel()
		searchJob = viewModelScope.launch {
			_state.update { it.copy(isSearching = true) }
			try {
				val results = gameRepository.searchGamesWithFilters(query, genres, platforms)
				_state.update { it.copy(searchResults = results, isSearching = false) }
			} catch (e: Exception) {
				_state.update {
					it.copy(
						searchResults = emptyList(), isSearching = false, error = e.localizedMessage
					)
				}
			}
		}
	}

	fun onSearchQueryChange(query: String) {
		_state.update { it.copy(searchQuery = query) }
	}

	fun toggleGenreId(genreId: Int) {
		_state.update { current ->
			val updated = current.selectedGenreIds.toMutableSet()
			if (!updated.remove(genreId)) updated.add(genreId)
			current.copy(selectedGenreIds = updated)
		}
	}

	fun togglePlatformId(platformId: Int) {
		_state.update { current ->
			val updated = current.selectedPlatformIds.toMutableSet()
			if (!updated.remove(platformId)) updated.add(platformId)
			current.copy(selectedPlatformIds = updated)
		}
	}

	fun clearFilters() {
		_state.update { it.copy(selectedGenreIds = emptySet(), selectedPlatformIds = emptySet()) }
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
}