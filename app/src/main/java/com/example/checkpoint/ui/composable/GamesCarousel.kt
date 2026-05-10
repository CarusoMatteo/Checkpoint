package com.example.checkpoint.ui.composable

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

data class LocalGame(
	val name: String,
	val publisher: String,
	val imageResourceId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesCarousel(
	title: String,
	modifier: Modifier = Modifier,
	carouselItems: List<LocalGame>,
	hasStartingDivider: Boolean = false
) {
	Column(modifier = modifier.padding(horizontal = 16.dp)) {
		if (hasStartingDivider) HorizontalDivider()

		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium
			)
			IconButton(
				onClick = { /* TODO: Open vertical carousel list */ }
			) {
				Icon(
					imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
					contentDescription = "See all"
				)
			}
		}

		HorizontalMultiBrowseCarousel(
			state = rememberCarouselState { carouselItems.count() },
			preferredItemWidth = 150.dp,
			itemSpacing = 8.dp,
			modifier = Modifier
				.fillMaxWidth()
				.wrapContentHeight()
				.padding(vertical = 8.dp)
		) {
			Box {
				AsyncImage(
					modifier = Modifier
						.maskClip(MaterialTheme.shapes.extraLarge)
						.size(150.dp, 200.dp)
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
						.combinedClickable(
							onClick = { /* TODO: Navigate to game details */ },
							onLongClick = { /* TODO: Show game options */ }
						),
					model = carouselItems[it].imageResourceId,
					contentDescription = carouselItems[it].name,
					contentScale = ContentScale.Crop
				)
				Column(
					modifier = Modifier
						.align(Alignment.BottomStart)
						.padding(horizontal = 12.dp)
						.padding(bottom = 8.dp)
				) {
					Text(
						text = carouselItems[it].name,
						style = MaterialTheme.typography.titleMedium,
						color = Color.White,
						maxLines = 2,
						overflow = TextOverflow.Ellipsis,
						modifier = Modifier
					)
					Text(
						text = carouselItems[it].publisher,
						style = MaterialTheme.typography.labelSmall,
						color = Color.White,
						maxLines = 1,
						overflow = TextOverflow.Ellipsis,
					)
				}
			}
		}
	}
}
