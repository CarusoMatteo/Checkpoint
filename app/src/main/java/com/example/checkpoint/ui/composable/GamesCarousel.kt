package com.example.checkpoint.ui.composable

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.LocalGame
import com.example.checkpoint.data.sampleLocalGames

@Composable
fun LazyGamesCarousel(
	title: String,
	games: List<LocalGame>,
	modifier: Modifier = Modifier,
	hasStartingDivider: Boolean = false,
	hasDeleteAction: Boolean = false
) {
	Column(modifier = modifier) {
		if (hasStartingDivider) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium,
				// overflow = TextOverflow.Ellipsis,
				maxLines = 1,
				modifier = Modifier
					.weight(1f)
					.basicMarquee()
			)
			if (hasDeleteAction) {
				IconButton(
					onClick = { /* TODO: Delete List */ }
				) {
					Icon(
						imageVector = Icons.Rounded.DeleteOutline,
						contentDescription = "See all"
					)
				}
			}
			IconButton(
				onClick = { /* TODO: Open vertical carousel list */ }
			) {
				Icon(
					imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
					contentDescription = "See all"
				)
			}
		}

		LazyRow(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 8.dp),
			contentPadding = PaddingValues(horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items(games) { game ->
				GameCover(
					game = game,
					showInformationOverlay = true,
					onClick = { /* TODO: Navigate to game details */ },
					onLongClick = { /* TODO: Show game options */ },
					modifier = Modifier.height(200.dp)
				)
			}
		}
	}
}

@Preview
@Composable
private fun LazyGamesCarouselPreview() {
	LazyGamesCarousel(
		title = "Since you liked ${sampleLocalGames.first().name}",
		games = sampleLocalGames,
		hasStartingDivider = true,
		hasDeleteAction = true
	)
}