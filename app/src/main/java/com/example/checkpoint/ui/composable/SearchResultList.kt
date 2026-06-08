package com.example.checkpoint.ui.composable

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import com.example.checkpoint.data.repositories.Game

@Composable
fun SearchResultList(
	games: List<Game>,
	modifier: Modifier = Modifier,
	navController: NavHostController,
	genres: List<GenreEntity> = emptyList(),
	platforms: List<PlatformEntity> = emptyList(),
	selectedGenreIds: Set<Int> = emptySet(),
	selectedPlatformIds: Set<Int> = emptySet(),
	onGenreToggle: (Int) -> Unit = {},
	onPlatformToggle: (Int) -> Unit = {}
) {
	// Show only the filter chips actively selected by the FiltersDrawer
	val genreChips = genres.filter { selectedGenreIds.contains(it.igdbId) }.map { genre ->
		ChipContent(
			label = genre.name, selected = true, action = { onGenreToggle(genre.igdbId) })
	}
	val platformChips =
		platforms.filter { selectedPlatformIds.contains(it.igdbId) }.map { platform ->
			ChipContent(
				label = platform.abbreviation ?: platform.name,
				selected = true,
				action = { onPlatformToggle(platform.igdbId) })
		}
	val allChips = genreChips + platformChips

	Column(modifier = modifier.fillMaxWidth()) {
		if (allChips.isNotEmpty()) {
			FilterChipRow(
				chips = allChips, modifier = Modifier.fillMaxWidth()
			)
		}
		LazyColumn(
			modifier = Modifier.fillMaxWidth()
		) {
			items(games) { game ->
				SearchResultItem(
					game = game,
					modifier = Modifier
						.padding(bottom = 8.dp)
						.fillMaxWidth()
						.clickable(onClick = {
							navController.navigate(
								NavigationRoute.GameScreen(game.igdbId)
							)
						})
						.padding(start = 16.dp)
				)
			}
		}
	}
}

@Composable
fun SearchResultItem(
	game: Game, modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier, verticalAlignment = Alignment.CenterVertically
	) {
		GameCover(
			game = game,
			showInformationOverlay = false,
			clickActions = null,
			modifier = Modifier.height(60.dp),
			clipShape = MaterialTheme.shapes.medium
		)
		Column(
			modifier = Modifier
				.padding(start = 12.dp)
				.fillMaxWidth()
		) {
			Text(
				text = game.name,
				style = MaterialTheme.typography.bodyLarge,
				maxLines = 1,
				modifier = Modifier.basicMarquee()
			)
			val companyText = when {
				!game.publisher.isNullOrBlank() && !game.developer.isNullOrBlank() &&
						game.publisher != game.developer -> "${game.developer} • ${game.publisher}"

				!game.publisher.isNullOrBlank() -> game.publisher
				!game.developer.isNullOrBlank() -> game.developer
				else -> "Unknown Company"
			}
			Text(
				text = companyText,
				style = MaterialTheme.typography.bodyMedium,
				maxLines = 1,
				modifier = Modifier.basicMarquee()
			)
		}
	}
}

@Preview
@Composable
private fun SearchResultListPreview() {
	SearchResultList(
		navController = rememberNavController(), games = listOf(
			Game(
				id = 0,
				igdbId = 123,
				name = "Example Game",
				summary = "This is an example game.",
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