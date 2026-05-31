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
	navController: NavHostController,
	vm: ExploreViewModel = koinViewModel()
) {
	val state by vm.state.collectAsStateWithLifecycle()

	AppShell(
		navController,
		title = "Explore",
		selectedNavigationItem = NavigationItem.Explore
	) { innerPadding ->

		// Mostra uno spinner centrale finché entrambe le sezioni stanno caricando
		if (state.isLoadingPopular && state.isLoadingRecent) {
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
			// Sezione ricerca — visibile solo se c'è una query attiva
			if (state.searchQuery.isNotBlank()) {
				LazyGamesCarousel(
					title = "Risultati per \"${state.searchQuery}\"",
					games = state.searchResults,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					}
				)
			} else {
				// Popular games da IGDB
				LazyGamesCarousel(
					title = "Popular right now",
					games = state.popularGames,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					}
				)

				// Uscite recenti da IGDB
				LazyGamesCarousel(
					title = "Recent releases",
					games = state.recentReleases,
					hasStartingDivider = true,
					onGameClick = { igdbId ->
						navController.navigate(NavigationRoute.GameScreen(igdbId))
					}
				)
			}
		}
	}
}