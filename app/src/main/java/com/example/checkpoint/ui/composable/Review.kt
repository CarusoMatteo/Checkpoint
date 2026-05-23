package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.Review
import com.example.checkpoint.data.sampleReviews

@Composable
fun Review(
	review: Review,
	modifier: Modifier = Modifier
) {
	OutlinedCard(
		modifier = modifier
	) {
		Column(
			modifier = Modifier.padding(horizontal = 16.dp)
		) {
			ReviewHeader(
				review,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 12.dp)
			)
			Text(
				text = review.comment,
				modifier = Modifier.padding(bottom = 16.dp)
			)
		}
	}
}

@Composable
fun ReviewHeader(
	review: Review,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Row(
			modifier = Modifier.weight(1f),
			verticalAlignment = Alignment.CenterVertically
		) {
			ProfilePicture(
				user = review.creator,
			)
			Column(
				modifier = Modifier
					.padding(start = 8.dp)
					.weight(1f)
			) {
				Text(
					text = review.creator.name,
					style = MaterialTheme.typography.titleMedium,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				ReviewRating(
					rating = review.rating
				)
			}
		}
		Text(
			text = review.completion.description,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(start = 8.dp)
		)
	}
}

@Preview
@Composable
private fun ReviewPreview() {
	Review(
		review = sampleReviews[0],
		modifier = Modifier
			.padding(horizontal = 16.dp)
			.fillMaxWidth()
	)
}