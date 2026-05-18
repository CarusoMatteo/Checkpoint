package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt

@Composable
fun ReviewScore(score: Float, modifier: Modifier = Modifier) {
	val roundedScore = score.coerceIn(0f, 5f).roundToHalf()
	Row(modifier = modifier) {
		for (i in 1..5) {
			Icon(
				imageVector = when {
					roundedScore >= i -> Icons.Rounded.Star
					roundedScore >= i - 0.5f -> Icons.AutoMirrored.Rounded.StarHalf
					else -> Icons.Rounded.StarOutline
				},
				contentDescription = null
			)
		}
	}
}

fun Float.roundToHalf(): Float {
	return (this * 2).roundToInt() / 2f
}
