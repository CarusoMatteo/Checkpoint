package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.sampleReviews
import com.example.checkpoint.data.Review as ReviewData

@Composable
fun ReviewList(
	title: String,
	reviews: List<ReviewData>,
	modifier: Modifier = Modifier,
	hasStartingDivider: Boolean = false
) {
	Column(modifier = modifier) {
		if (hasStartingDivider) HorizontalDivider()

		Text(
			text = title,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier
				.padding(vertical = 8.dp)
				.defaultMinSize(minHeight = 48.dp)
		)
		reviews.forEach {
			Review(
				review = it,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth()
			)
		}
	}
}

@Preview
@Composable
private fun ReviewListPreview() {
	ReviewList(
		title = "Reviews",
		reviews = sampleReviews,
		modifier = Modifier.padding(horizontal = 16.dp),
		hasStartingDivider = true
	)
}