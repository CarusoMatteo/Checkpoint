package com.example.checkpoint.ui.composable

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.checkpoint.data.LocalGame
import com.example.checkpoint.data.sampleLocalGames

@Composable
fun GameCover(
	game: LocalGame,
	showInformationOverlay: Boolean,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	modifier: Modifier = Modifier,
	nameMaxLines: Int = 2,
	publisherMaxLines: Int = 1
) {
	Box(
		modifier = modifier
			.aspectRatio(3f / 4f)
			.clip(MaterialTheme.shapes.extraLarge)
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
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
							colors = if (showInformationOverlay)
								listOf(
									Color.Black.copy(alpha = 0.0f),
									Color.Black.copy(alpha = 0.0f),
									Color.Black.copy(alpha = 1.0f)
								)
							else
								listOf(
									Color.Transparent,
									Color.Transparent
								)
						)
					)
				}
		)
		if (showInformationOverlay) {
			Column(
				modifier = Modifier
					.align(Alignment.BottomStart)
					.padding(horizontal = 12.dp, vertical = 8.dp)
			) {
				Text(
					text = game.name,
					style = MaterialTheme.typography.titleSmall,
					color = Color.White,
					maxLines = nameMaxLines,
					overflow = TextOverflow.Ellipsis
				)
				Text(
					text = game.publisher,
					style = MaterialTheme.typography.labelSmall,
					color = Color.White,
					maxLines = publisherMaxLines,
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	}
}

@Preview
@Composable
private fun GameCoverPreview() {
	GameCover(
		game = sampleLocalGames[0],
		showInformationOverlay = true,
		onClick = { },
		onLongClick = { },
		modifier = Modifier.height(200.dp)
	)
}