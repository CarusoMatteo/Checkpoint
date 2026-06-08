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
import com.example.checkpoint.data.database.DatabaseSeeder
// Importiamo le Entity corrette del database
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserEntity

@Composable
fun Review(
	review: ReviewEntity,
	user: UserEntity,
	modifier: Modifier = Modifier
) {
	OutlinedCard(modifier = modifier) {
		Column(modifier = Modifier.padding(horizontal = 16.dp)) {
			ReviewHeader(
				review = review,
				user = user,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 12.dp)
			)
			Text(
				text = review.body,
				modifier = Modifier.padding(bottom = 16.dp)
			)
		}
	}
}

@Composable
fun ReviewHeader(
	review: ReviewEntity,
	user: UserEntity,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier,
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Row(
			modifier = Modifier
				.weight(1f),
			verticalAlignment = Alignment.CenterVertically
		) {
			ProfilePicture(
				user = user,
			)
			Column(
				modifier = Modifier
					.padding(start = 8.dp)
			) {
				Text(
					text = user.username,
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
			text = review.completionEnum?.displayName ?: review.completion,
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier
				.padding(start = 8.dp)
		)
	}
}

@Preview()
@Composable
private fun ReviewPreview() {
	val previewUser = DatabaseSeeder.users.first()
	val previewReview = DatabaseSeeder.sampleDbReviews.first()

	Review(
		review = previewReview,
		user = previewUser,
		modifier = Modifier
			.padding(16.dp)
			.fillMaxWidth()
	)
}