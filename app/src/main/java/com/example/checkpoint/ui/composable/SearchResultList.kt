package com.example.checkpoint.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.repositories.Game

@Composable
fun SearchResultScreen(
	games: List<Game>,
	modifier: Modifier = Modifier
) {
	val selectedChips = listOf(
		ChipContent("Action", selected = true, action = { }),
		ChipContent("Adventure", selected = true, action = { }),
		ChipContent("RPG", selected = true, action = { }),
		ChipContent("Strategy", selected = true, action = { }),
		ChipContent("Simulation", selected = true, action = { })
	)

	FilterChipRow(
		chips = selectedChips,
		modifier = modifier
	)
}

@Composable
fun SearchResultItem(modifier: Modifier = Modifier) {

}

@Preview
@Composable
private fun SearchResultScreenPreview() {
	SearchResultScreen(
		games = listOf(
			Game(
				id = 0,
				igdbId = 123,
				name = "Example Game",
				summary = "This is an example game used for previewing the SearchResultScreen composable.",
				coverUrl = null,
				genres = listOf("Action", "Adventure"),
				platforms = listOf("PC", "PlayStation 5"),
				developer = "Example Studios",
				publisher = "Example Publishing",
				firstReleaseDate = 1700000000000,
				totalRating = 85.5,
				totalRatingCount = 1000
			)
		)
	)
}
