package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.composable.GamesCarousel

@Composable
fun ExploreScreen(
	navController: NavHostController,
	modifier: Modifier = Modifier
) {
	val scrollState = rememberScrollState()

	Column(
		modifier = modifier.verticalScroll(scrollState)
	) {
		GamesCarousel(
			title = "Popular right now",
			carouselItems = sampleLocalGames
		)
		GamesCarousel(
			title = "Coming soon",
			carouselItems = sampleLocalGames,
			hasStartingDivider = true
		)
	}
}