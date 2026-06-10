package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// IGDB platform IDs for the "best on platform" carousels
private const val PLATFORM_PC = 6   // PC
private const val PLATFORM_PS5 = 167 // PS 5

data class ExploreState(
	val popularGames: List<Game> = emptyList(),
	val recentReleases: List<Game> = emptyList(),
	val comingSoonGames: List<Game> = emptyList(),
	val becauseYouPlayed: List<Game> = emptyList(),
	val becauseYouPlayedGameName: String = "",
	val bestOnPc: List<Game> = emptyList(),
	val bestOnConsole: List<Game> = emptyList(),
	val sinceYouLikeGenre: List<Game> = emptyList(),
	val sinceYouLikeGenreName: String = "",
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
	private val gameRepository: GameRepository,
	private val reviewDao: ReviewDao,
	private val sessionManager: SessionManager,
	private val genreDao: GenreDao
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
		loadComingSoonGames()
		loadPersonalizedCarousels()
		loadGenresAndPlatforms()
	}

	private fun loadGenresAndPlatforms() {
		// 1. Remote Synchronization (IGDB -> Room)
		viewModelScope.launch {
			try {
				gameRepository.fetchGenresFromIgdb()
			} catch (e: Exception) {
				_state.update { it.copy(error = "Error loading genres: ${e.message}") }
			}
		}
		viewModelScope.launch {
			try {
				gameRepository.fetchPlatformsFromIgdb()
			} catch (e: Exception) {
				_state.update { it.copy(error = "Error loading platforms: ${e.message}") }
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

	/**
	 * Loads the three personalized carousels that depend on the logged-in user:
	 * - Because you played: similar games to those the user reviewed best
	 * - The best on PC / PS5: top-rated games by platform
	 * - Since you like: top games in the user's first preferred genre
	 */
	private fun loadPersonalizedCarousels() {
		viewModelScope.launch {
			_state.update { it.copy(isLoadingRecommendations = true) }

			val session = sessionManager.sessionState.first { it !is SessionState.Loading }

			// ── Because you played ────────────────────────────────────────────
			var becauseYouPlayed: List<Game> = emptyList()
			var becauseYouPlayedGameName = ""

			if (session is SessionState.LoggedIn) {
				val reviews = reviewDao.getReviewsByUser(session.userId).firstOrNull()
				// Pick the review with the highest rating
				val topReview = reviews?.maxByOrNull { it.rating }

				if (topReview != null) {
					val gameEntity = gameRepository.getLocalEntityById(topReview.gameId)
					if (gameEntity != null) {
						val similarGames = gameRepository.getSimilarGames(gameEntity.igdbId)
						if (similarGames.isNotEmpty()) {
							// Fetch the full game name to display in the carousel title
							val sourceGame = gameRepository.fetchGameDetails(gameEntity.igdbId)
							becauseYouPlayedGameName = sourceGame?.name ?: ""
							becauseYouPlayed = similarGames
						}
					}
				}
				// If no reviews carousel stays hidden
			}

			// ── Best on PC & PS5
			val bestOnPc = gameRepository.getGamesByPlatform(PLATFORM_PC, limit = 20)
			val bestOnConsole = gameRepository.getGamesByPlatform(PLATFORM_PS5, limit = 20)

			// ── Since you like
			var sinceYouLikeGames: List<Game> = emptyList()
			var sinceYouLikeGenreName = ""

			if (session is SessionState.LoggedIn) {
				// Fetch the user's preferred genres and pick the first one
				val preferredGenres =
					genreDao.getPreferredGenresForUser(session.userId).firstOrNull()
				val firstGenre = preferredGenres?.firstOrNull()

				if (firstGenre != null) {
					sinceYouLikeGenreName = firstGenre.name
					sinceYouLikeGames =
						gameRepository.getGamesByGenre(firstGenre.igdbId, limit = 20)
				}
			}

			_state.update {
				it.copy(
					becauseYouPlayed = becauseYouPlayed,
					becauseYouPlayedGameName = becauseYouPlayedGameName,
					bestOnPc = bestOnPc,
					bestOnConsole = bestOnConsole,
					sinceYouLikeGenre = sinceYouLikeGames,
					sinceYouLikeGenreName = sinceYouLikeGenreName,
					isLoadingRecommendations = false
				)
			}
		}
	}

	@OptIn(FlowPreview::class)
	private fun observeSearchTriggers() {
		// Observe Text, Genres, and Platforms simultaneously.
		// If any of the three changes, start the filtered search
		viewModelScope.launch {
			_state.map { Triple(it.searchQuery, it.selectedGenreIds, it.selectedPlatformIds) }
				.distinctUntilChanged().debounce(400) // wait
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
}