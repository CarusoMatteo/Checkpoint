package com.example.checkpoint.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.R

data class CarouselItem(
	val imageResId: Int,
	val contentDescription: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesCarousel(
	title: String,
	modifier: Modifier = Modifier,
	items: List<CarouselItem>
) {
	Column(modifier = modifier.padding(horizontal = 16.dp)) {
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
			state = rememberCarouselState { items.count() },
			preferredItemWidth = 150.dp,
			itemSpacing = 8.dp,
			modifier = Modifier.padding(vertical = 8.dp)
		) {
			Image(
				modifier = Modifier
					.size(150.dp, 200.dp)
					.clickable(onClick = { /* TODO: Navigate to game details */ })
					.maskClip(MaterialTheme.shapes.extraLarge),
				painter = painterResource(id = items[it].imageResId),
				contentDescription = items[it].contentDescription,
				contentScale = ContentScale.Crop
			)
		}
	}
}

@Preview
@Composable
fun GamesCarouselPreview() {
	Column {
		GamesCarousel(
			title = "Popular right now",
			items = listOf(
				CarouselItem(
					R.drawable.re9,
					"Resident Evil Requiem"
				),
				CarouselItem(
					R.drawable.pgm,
					"Pragmata"
				),
				CarouselItem(
					R.drawable.re8,
					"Resident Evil Village"
				),
				CarouselItem(
					R.drawable.fl,
					"007 First Light"
				),
				CarouselItem(
					R.drawable.tmdltd,
					"Tomodachi Life: Living the Dream"
				)
			)
		)
		GamesCarousel(
			title = "Coming soon",
			items = listOf(
				CarouselItem(
					R.drawable.re9,
					"Resident Evil Requiem"
				),
				CarouselItem(
					R.drawable.pgm,
					"Pragmata"
				),
				CarouselItem(
					R.drawable.re8,
					"Resident Evil Village"
				),
				CarouselItem(
					R.drawable.fl,
					"007 First Light"
				),
				CarouselItem(
					R.drawable.tmdltd,
					"Tomodachi Life: Living the Dream"
				)
			)
		)
	}
}
