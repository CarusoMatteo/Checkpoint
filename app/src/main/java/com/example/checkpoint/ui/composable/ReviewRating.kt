package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ReviewRating(
	rating: Float,
	modifier: Modifier = Modifier,
	iconSize: Dp = 24.dp
) {
	val roundedRating = rating.coerceIn(0f, 5f).roundToHalf()
	Row(modifier = modifier) {
		for (i in 1..5) {
			Icon(
				imageVector = when {
					roundedRating >= i -> Icons.Rounded.Star
					roundedRating >= i - 0.5f -> Icons.AutoMirrored.Rounded.StarHalf
					else -> Icons.Rounded.StarOutline
				},
				contentDescription = null,
				modifier = Modifier.size(iconSize)
			)
		}
	}
}

fun Float.roundToHalf(): Float {
	return (this * 2).roundToInt() / 2f
}
