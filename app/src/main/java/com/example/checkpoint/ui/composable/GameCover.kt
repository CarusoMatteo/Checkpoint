package com.example.checkpoint.ui.composable

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
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
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.sampleLocalGames

data class ClickActions(
	val onClick: () -> Unit,
	val onLongClick: () -> Unit
)

/**
 * Core game cover component that handles image rendering and text overlays.
 *
 * Accepts a generic [model] parameter (such as a local drawable resource ID or a remote URL string)
 * allowing Coil to load different image sources seamlessly without duplicating layout logic.
 */
@Composable
fun GameCover(
	model: Any?,
	name: String,
	publisher: String?,
	showInformationOverlay: Boolean,
	clickActions: ClickActions?,
	clipShape: CornerBasedShape,
	modifier: Modifier = Modifier,
	nameMaxLines: Int = 2,
	publisherMaxLines: Int = 1
) {
	Box(
		modifier = if (clickActions != null) modifier
			.aspectRatio(3f / 4f)
			.clip(clipShape)
			.combinedClickable(
				onClick = clickActions.onClick, onLongClick = clickActions.onLongClick
			)
		else modifier
			.aspectRatio(3f / 4f)
			.clip(clipShape)
	) {
		AsyncImage(
			model = model,
			contentDescription = name,
			contentScale = ContentScale.Crop,
			modifier = Modifier
				.fillMaxSize()
				.drawWithContent {
					drawContent()
					drawRect(
						Brush.verticalGradient(
							colors = if (showInformationOverlay) listOf(
								Color.Black.copy(alpha = 0.0f),
								Color.Black.copy(alpha = 0.0f),
								Color.Black.copy(alpha = 1.0f)
							)
							else listOf(Color.Transparent, Color.Transparent)
						)
					)
				})
		if (showInformationOverlay) {
			Column(
				modifier = Modifier
					.align(Alignment.BottomStart)
					.padding(horizontal = 12.dp, vertical = 8.dp)
			) {
				Text(
					text = name,
					style = MaterialTheme.typography.titleSmall,
					color = Color.White,
					maxLines = nameMaxLines,
					overflow = TextOverflow.Ellipsis
				)
				if (!publisher.isNullOrBlank()) {
					Text(
						text = publisher,
						style = MaterialTheme.typography.labelSmall,
						color = Color.White,
						maxLines = publisherMaxLines,
						overflow = TextOverflow.Ellipsis
					)
				}
			}
		}
	}
}

/**
 * Overload for [LocalGame] that uses a local drawable resource ID.
 * Maintained for backward compatibility with existing features like GamesGridScreen.
 */
@Composable
fun GameCover(
	game: LocalGame,
	showInformationOverlay: Boolean,
	clickActions: ClickActions?,
	modifier: Modifier = Modifier,
	nameMaxLines: Int = 2,
	publisherMaxLines: Int = 1,
	clipShape: CornerBasedShape = MaterialTheme.shapes.extraLarge
) = GameCover(
	model = game.imageResourceId,
	name = game.name,
	publisher = game.publisher,
	showInformationOverlay = showInformationOverlay,
	clickActions = clickActions,
	modifier = modifier,
	nameMaxLines = nameMaxLines,
	publisherMaxLines = publisherMaxLines,
	clipShape = clipShape
)

/**
 * Overload for domain [Game] entities that resolves to a remote cover URL from IGDB.
 */
@Composable
fun GameCover(
	game: Game,
	showInformationOverlay: Boolean,
	clickActions: ClickActions?,
	modifier: Modifier = Modifier,
	nameMaxLines: Int = 2,
	publisherMaxLines: Int = 1,
	clipShape: CornerBasedShape = MaterialTheme.shapes.extraLarge,
) = GameCover(
	model = game.coverUrl,
	name = game.name,
	publisher = game.publisher ?: game.developer,
	showInformationOverlay = showInformationOverlay,
	clickActions = clickActions,
	modifier = modifier,
	nameMaxLines = nameMaxLines,
	publisherMaxLines = publisherMaxLines,
	clipShape = clipShape
)

@Preview
@Composable
private fun GameCoverPreview() {
	GameCover(
		game = sampleLocalGames[0],
		showInformationOverlay = true,
		clickActions = ClickActions({}, {}),
		modifier = Modifier.height(200.dp)
	)
}