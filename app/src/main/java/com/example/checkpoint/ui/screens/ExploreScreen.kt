package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.checkpoint.R
import com.example.checkpoint.ui.composable.GamesCarousel
import com.example.checkpoint.ui.composable.LocalGame

val carouselItems = listOf(
	LocalGame(
		"The Legend of Zelda: Breath of the Wild - Nintendo Switch 2 Edition",
		"Nintendo",
		R.drawable.botw
	),
	LocalGame("Resident Evil Requiem", "CAPCOM", R.drawable.re9),
	LocalGame("Pragmata", "CAPCOM", R.drawable.pgm),
	LocalGame("Resident Evil Village", "CAPCOM", R.drawable.re8),
	LocalGame("007 First Light", "IO Interactive", R.drawable.fl),
	LocalGame("Tomodachi Life: Living the Dream", "Nintendo", R.drawable.tmdltd),
	LocalGame(
		"The Legend of Zelda: Breath of the Wild - Nintendo Switch 2 Edition",
		"Nintendo",
		R.drawable.botw
	),
	LocalGame("Resident Evil Requiem", "CAPCOM", R.drawable.re9),
	LocalGame("Pragmata", "CAPCOM", R.drawable.pgm),
	LocalGame("Resident Evil Village", "CAPCOM", R.drawable.re8),
)

@Composable
fun ExploreScreen(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
	) {
		GamesCarousel(
			title = "Popular right now",
			carouselItems = carouselItems
		)
		GamesCarousel(
			title = "Coming soon",
			carouselItems = carouselItems,
			hasStartingDivider = true
		)
	}
}