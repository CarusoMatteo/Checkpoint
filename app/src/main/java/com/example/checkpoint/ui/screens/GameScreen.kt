package com.example.checkpoint.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.checkpoint.ui.composable.ReviewScore
import com.example.checkpoint.ui.composable.SmallSplitButtons
import java.time.format.FormatStyle

@Composable
fun GameScreen(
	navController: NavHostController,
	modifier: Modifier = Modifier,
) {
	val game = sampleLocalGames.first()

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp)
	) {
		GameHeader(
			game = game,
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 8.dp)
		)
		GameScreenSection(
			title = "Description",
			contentText = game.description,
			modifier = Modifier.padding(vertical = 8.dp)
		)
		GameScreenSection(
			title = "Release date",
			contentText = game.releaseDate.toLocalFormat(FormatStyle.LONG),
			modifier = Modifier.padding(vertical = 8.dp)
		)
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
				maxLines = 2,
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

@Composable
fun GameScreenSection(
	title: String,
	contentText: String,
	modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Text(
			text = title,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(bottom = 8.dp)
		)
		Text(
			text = contentText,
			style = MaterialTheme.typography.bodyMedium
		)
	}
}