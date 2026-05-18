package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Column
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
	Column(
		modifier = modifier
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