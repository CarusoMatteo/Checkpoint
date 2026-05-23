package com.example.checkpoint.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.checkpoint.data.LocalGame
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.data.toLocalFormat
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.GamesCarousel
import com.example.checkpoint.ui.composable.LabeledChipRow
import com.example.checkpoint.ui.composable.LabeledText
import com.example.checkpoint.ui.composable.LabeledTextWithAction
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.ReviewScore
import com.example.checkpoint.ui.composable.SmallSplitButtons
import java.time.format.FormatStyle

@Composable
fun GameScreen(
	navController: NavHostController,
	game: LocalGame,
) {
	val scrollState = rememberScrollState()

	AppShell(
		navController,
		title = "Game",
		selectedNavigationItem = NavigationItem.Explore
	)
	{ innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
				.verticalScroll(scrollState)
		) {
			GameHeader(
				game = game,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp)
			)
			LabeledText(
				title = "Description",
				contentText = game.description,
				modifier = Modifier
					.padding(vertical = 8.dp, horizontal = 16.dp)
					.fillMaxWidth()
			)
			// TODO: If game has already released, show LabeledText instead of LabeledTextWithAction.
			// Otherwise, if game.releaseDate is in the future, show release date with "Add to calendar" action
			LabeledTextWithAction(
				title = "Release date",
				contentText = game.releaseDate.toLocalFormat(FormatStyle.LONG),
				actionText = "Add to calendar",
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp, horizontal = 16.dp)
			) { /* TODO: Add to calendar action */ }
			LabeledChipRow(
				title = "Genres",
				chips = game.genres,
				modifier = Modifier.padding(vertical = 8.dp)
			)
			GamesCarousel(
				title = "From the series",
				carouselItems = sampleLocalGames,
				hasStartingDivider = true
			)
		}
	}
}

@Composable
private fun GameHeader(game: LocalGame, modifier: Modifier = Modifier) {
	Row(modifier = modifier) {
		AsyncImage(
			modifier = Modifier
				.clip(MaterialTheme.shapes.extraLarge)
				.width(width = 100.dp),
			contentScale = ContentScale.FillWidth,
			model = game.imageResourceId,
			contentDescription = game.name,
		)

		Column(
			modifier = Modifier.padding(start = 16.dp),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			Text(
				text = game.name,
				style = MaterialTheme.typography.headlineSmall,
				modifier = Modifier.basicMarquee()
			)
			Text(
				text = game.publisher,
				style = MaterialTheme.typography.titleMedium
			)
			ReviewScore(
				score = 4.2f,
				modifier = Modifier.fillMaxWidth()
			)
			SmallSplitButtons(
				onPrimaryClick = { },
				onSecondaryClick = { },
				primaryIcon = {
					Icon(
						imageVector = Icons.Rounded.AddCircleOutline,
						contentDescription = null
					)
				},
				primaryLabel = "Add to Backlog",
				secondaryIcon = {
					Icon(
						imageVector = Icons.Rounded.KeyboardArrowDown,
						contentDescription = null
					)
				}
			)
		}
	}
}