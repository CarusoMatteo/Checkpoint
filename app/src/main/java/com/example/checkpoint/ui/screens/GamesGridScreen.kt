package com.example.checkpoint.ui.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.checkpoint.data.LocalGame
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.NavigationItem

@Composable
fun GamesGridScreen(
	navController: NavHostController,
	title: String,
	games: List<LocalGame>,
	modifier: Modifier = Modifier
) {
	AppShell(
		navController,
		title = title,
		selectedNavigationItem = NavigationItem.Explore
	)
	{ innerPadding ->
		LazyVerticalGrid(
			columns = GridCells.Fixed(2),
			contentPadding = PaddingValues(16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			items(games) { game ->
				GameGridCard(game = game)
			}
		}
	}
}

@Composable
fun GameGridCard(
	game: LocalGame,
	modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier
			.fillMaxWidth()
			.aspectRatio(3f / 4f)
			.clip(MaterialTheme.shapes.extraLarge)
			.combinedClickable(
				onClick = { /* TODO: Game details redirect */ },
				onLongClick = { /* TODO: May open up Multiple choice window */ }
			)
	) {
		AsyncImage(
			model = game.imageResourceId,
			contentDescription = game.name,
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.fillMaxSize()
				.drawWithContent {
					drawContent()
					drawRect(
						Brush.verticalGradient(
							colors = listOf(
								Color.Black.copy(alpha = 0.0f),
								Color.Black.copy(alpha = 0.0f),
								Color.Black.copy(alpha = 1.0f)
							)
						)
					)
				}
		)
		Column(
			modifier = Modifier
				.align(Alignment.BottomStart)
				.padding(horizontal = 12.dp, vertical = 8.dp)
		) {
			Text(
				text = game.name,
				style = MaterialTheme.typography.titleSmall,
				color = Color.White,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis
			)
			Text(
				text = game.publisher,
				style = MaterialTheme.typography.labelSmall,
				color = Color.White,
				maxLines = 1,
				overflow = TextOverflow.Ellipsis
			)
		}
	}
}