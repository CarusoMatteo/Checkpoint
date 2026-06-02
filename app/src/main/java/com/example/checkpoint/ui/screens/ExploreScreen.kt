package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.LazyGamesCarousel
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.viewmodel.ExploreViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExploreScreen(
	navController: NavHostController, vm: ExploreViewModel = koinViewModel()
) {
	val state by vm.state.collectAsStateWithLifecycle()

	AppShell(
		navController, title = "Explore", selectedNavigationItem = NavigationItem.Explore
	) { innerPadding ->

		// Mostra lo spinner se tutte le sezioni principali sono ancora in caricamento continuo
		if (state.isLoadingPopular && state.isLoadingRecent && state.isLoadingRecommendations) {
			Box(
				modifier = Modifier
					.padding(innerPadding)
					.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
			return@AppShell
		}

		val scrollState = rememberScrollState()

		Column(
			modifier = Modifier
				.padding(innerPadding)
				.verticalScroll(scrollState)
		) {


			// 1. Popular games
			if (state.popularGames.isNotEmpty()) {
				LazyGamesCarousel(
					title = "Popular right now",
					games = state.popularGames,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					})
			}

			// 2. Coming Soon
			if (state.comingSoonGames.isNotEmpty()) {
				LazyGamesCarousel(
					title = "Coming soon",
					games = state.comingSoonGames,
					hasStartingDivider = true,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					})
			}

			// 3. Recent releases
			if (state.recentReleases.isNotEmpty()) {
				LazyGamesCarousel(
					title = "Recent releases",
					games = state.recentReleases,
					hasStartingDivider = true,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					})
			}

			// 4. Because you played...
			if (state.becauseYouPlayed.isNotEmpty()) {
				LazyGamesCarousel(
					title = "Because you played",
					games = state.becauseYouPlayed,
					hasStartingDivider = true,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					})
			}

			// 5. The best on "Platform"
			if (state.bestOnPlatform.isNotEmpty()) {
				LazyGamesCarousel(
					title = "The best on PC",
					games = state.bestOnPlatform,
					hasStartingDivider = true,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					})
			}

			// 6. Since you like "Genre"
			if (state.sinceYouLikeGenre.isNotEmpty()) {
				LazyGamesCarousel(
					title = "Since you like RPG",
					games = state.sinceYouLikeGenre,
					hasStartingDivider = true,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					})
			}
			//7 Since you searched
			if (state.searchQuery.isNotBlank()) {
				LazyGamesCarousel(
					title = "Since you Serched \"${state.searchQuery}\"",
					games = state.searchResults,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					})
			}
		}
	}
}
