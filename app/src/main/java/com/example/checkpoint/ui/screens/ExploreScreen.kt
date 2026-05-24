package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.LazyGamesCarousel
import com.example.checkpoint.ui.composable.NavigationItem

@Composable
fun ExploreScreen(
	navController: NavHostController
) {
	AppShell(
		navController,
		title = "Explore",
		selectedNavigationItem = NavigationItem.Explore
	) { innerPadding ->
		val scrollState = rememberScrollState()

		Column(
			modifier = Modifier
				.padding(innerPadding)
				.verticalScroll(scrollState)
		) {
			LazyGamesCarousel(
				title = "Popular right now",
				games = sampleLocalGames
			)
			LazyGamesCarousel(
				title = "Coming soon",
				games = sampleLocalGames,
				hasStartingDivider = true
			)
		}
	}
}