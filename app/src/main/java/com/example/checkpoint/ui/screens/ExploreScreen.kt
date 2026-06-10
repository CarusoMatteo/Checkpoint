package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.FiltersDrawer
import com.example.checkpoint.ui.composable.LazyGamesCarousel
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.SearchAppShell
import com.example.checkpoint.ui.composable.SearchResultList
import com.example.checkpoint.ui.viewmodel.ExploreViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreScreen(
	navController: NavHostController, vm: ExploreViewModel = koinViewModel()
) {
	val state by vm.state.collectAsStateWithLifecycle()
	val isFiltersDrawerOpen = remember { mutableStateOf(false) }

	// Bottom Sheet Drawer for filter selection (Genres and Platforms)
	if (isFiltersDrawerOpen.value) {
		FiltersDrawer(
			showBottomSheet = isFiltersDrawerOpen,
			genres = state.availableGenres,
			platforms = state.availablePlatforms,
			selectedGenreIds = state.selectedGenreIds,
			selectedPlatformIds = state.selectedPlatformIds,
			onGenreToggle = { vm.toggleGenreId(it) },
			onPlatformToggle = { vm.togglePlatformId(it) },
			onResetAll = { vm.clearFilters() })
	}

	SearchAppShell(
		navController = navController,
		title = "Explore",
		selectedNavigationItem = NavigationItem.Explore,
		isFiltersDrawerOpen = isFiltersDrawerOpen,
		mainContent = {
			// Show the spinner if all major sections are still in continuous loading
			if (state.isLoadingPopular && state.isLoadingRecent && state.isLoadingRecommendations) {
				Box(
					modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
				return@SearchAppShell
			}

			val scrollState = rememberScrollState()
			val navigateToGrid: (String, List<Game>) -> Unit = { title, gamesList ->
				navController.currentBackStackEntry?.savedStateHandle?.set("grid_games", gamesList)
				navController.navigate(NavigationRoute.GamesGridScreen(title))
			}

			Column(modifier = Modifier.verticalScroll(scrollState)) {

				// 1. Popular right now
				if (state.popularGames.isNotEmpty()) {
					LazyGamesCarousel(
						title = "Popular right now",
						games = state.popularGames,
						onGameClick = { navController.navigate(NavigationRoute.GameScreen(it)) },
						onSeeAllClick = { navigateToGrid("Popular right now", state.popularGames) })
				}

				// 2. Coming Soon
				if (state.comingSoonGames.isNotEmpty()) {
					LazyGamesCarousel(
						title = "Coming soon",
						games = state.comingSoonGames,
						hasStartingDivider = true,
						onGameClick = { navController.navigate(NavigationRoute.GameScreen(it)) },
						onSeeAllClick = { navigateToGrid("Coming soon", state.comingSoonGames) })
				}

				// 3. Recent releases
				if (state.recentReleases.isNotEmpty()) {
					LazyGamesCarousel(
						title = "Recent releases",
						games = state.recentReleases,
						hasStartingDivider = true,
						onGameClick = { navController.navigate(NavigationRoute.GameScreen(it)) },
						onSeeAllClick = { navigateToGrid("Recent releases", state.recentReleases) })
				}

				// 4. Because you played (based on user's highest-rated review)
				if (state.becauseYouPlayed.isNotEmpty()) {
					LazyGamesCarousel(
						title = "Because you played \"${state.becauseYouPlayedGameName}\"",
						games = state.becauseYouPlayed,
						hasStartingDivider = true,
						onGameClick = { navController.navigate(NavigationRoute.GameScreen(it)) },
						onSeeAllClick = {
							navigateToGrid(
								"Because you played", state.becauseYouPlayed
							)
						})
				}

				// 5. The best on PC
				if (state.bestOnPc.isNotEmpty()) {
					LazyGamesCarousel(
						title = "The best on PC",
						games = state.bestOnPc,
						hasStartingDivider = true,
						onGameClick = { navController.navigate(NavigationRoute.GameScreen(it)) },
						onSeeAllClick = { navigateToGrid("The best on PC", state.bestOnPc) })
				}

				// 6. The best on PS5
				if (state.bestOnConsole.isNotEmpty()) {
					LazyGamesCarousel(
						title = "The best on PS5",
						games = state.bestOnConsole,
						hasStartingDivider = true,
						onGameClick = { navController.navigate(NavigationRoute.GameScreen(it)) },
						onSeeAllClick = { navigateToGrid("The best on PS5", state.bestOnConsole) })
				}

				// 7. Since you like <genre> (based on user's first preferred genre)
				if (state.sinceYouLikeGenre.isNotEmpty()) {
					LazyGamesCarousel(
						title = "Since you like ${state.sinceYouLikeGenreName}",
						games = state.sinceYouLikeGenre,
						hasStartingDivider = true,
						onGameClick = { navController.navigate(NavigationRoute.GameScreen(it)) },
						onSeeAllClick = {
							navigateToGrid(
								"Since you like ${state.sinceYouLikeGenreName}",
								state.sinceYouLikeGenre
							)
						})
				}
			}
		},
		searchContent = {
			if (state.isSearching) {
				Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
					CircularProgressIndicator()
				}
			} else {
				SearchResultList(
					games = state.searchResults,
					navController = navController,
					genres = state.availableGenres,
					platforms = state.availablePlatforms,
					selectedGenreIds = state.selectedGenreIds,
					selectedPlatformIds = state.selectedPlatformIds,
					onGenreToggle = { vm.toggleGenreId(it) },
					onPlatformToggle = { vm.togglePlatformId(it) })
			}
		},
		onSearch = { query -> vm.onSearchQueryChange(query) })
}