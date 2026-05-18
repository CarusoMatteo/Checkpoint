package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.checkpoint.data.LocalGame
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.composable.ReviewScore

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
			modifier = Modifier.fillMaxWidth()
		)
	}
}

@Composable
private fun GameHeader(game: LocalGame, modifier: Modifier = Modifier) {
	Row(modifier = modifier) {
		AsyncImage(
			modifier = Modifier
				.clip(MaterialTheme.shapes.extraLarge)
				.size(150.dp, 200.dp),
			model = game.imageResourceId,
			contentDescription = game.name,
			contentScale = ContentScale.Crop
		)

		Column(
			modifier = Modifier.padding(start = 16.dp)
		) {
			Text(
				text = game.name,
				style = MaterialTheme.typography.headlineSmall,
				overflow = TextOverflow.Ellipsis,
				maxLines = 2
			)
			Text(
				text = game.publisher,
				style = MaterialTheme.typography.titleMedium
			)
			ReviewScore(
				score = 4.2f,
				modifier = Modifier.fillMaxWidth()
			)
		}
	}
}