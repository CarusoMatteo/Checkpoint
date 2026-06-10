package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.checkpoint.data.database.DatabaseSeeder
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.repositories.CompletionType
import com.example.checkpoint.ui.icons.Reviews

@Composable
fun ReviewList(
	title: String,
	reviews: List<ReviewEntity>,
	users: Map<Int, UserEntity> = emptyMap(),
	modifier: Modifier = Modifier,
	hasStartingDivider: Boolean = false,
	hasWriteReviewButton: Boolean = true,
	onReviewClick: ((ReviewEntity) -> Unit)? = null,
	onReviewSubmit: (Float, String, CompletionType) -> Unit = { _, _, _ -> }
) {
	var showWriteReviewDialog by remember { mutableStateOf(false) }

	Column(modifier = modifier) {
		if (hasStartingDivider) HorizontalDivider()

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.defaultMinSize(minHeight = 48.dp)
				.padding(vertical = 8.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = title, style = MaterialTheme.typography.titleMedium
			)
			if (hasWriteReviewButton) {
				FilledTonalButton(onClick = { showWriteReviewDialog = true }) {
					Icon(
						imageVector = Reviews,
						contentDescription = "Leave a review",
					)
					Text(
						text = "Leave a review", modifier = Modifier.padding(start = 16.dp)
					)
				}
			}
		}

		reviews.forEach { review ->

			val reviewUser = users[review.userId] ?: UserEntity(
				id = review.userId,
				username = "Gamer_${review.userId}",
				email = "",
				passwordHash = "",
				bio = null,
				publicProfile = true,
				createdAt = ""
			)

			Review(
				review = review,
				user = reviewUser,
				modifier = Modifier
					.padding(bottom = 8.dp)
					.fillMaxWidth(),
				onClick = onReviewClick?.let { cb -> { cb(review) } })
		}
	}

	if (showWriteReviewDialog) {
		WriteReviewDialog(
			onDismissRequest = { showWriteReviewDialog = false },
			onSubmit = { rating, body, completion ->
				onReviewSubmit(rating, body, completion)
				showWriteReviewDialog = false
			})
	}
}

@Preview(showBackground = true)
@Composable
private fun ReviewListPreview() {
	val previewReviews = DatabaseSeeder.sampleDbReviews
	val previewUsersMap = DatabaseSeeder.users.associateBy { it.id }

	ReviewList(
		title = "Reviews",
		reviews = previewReviews,
		users = previewUsersMap,
		modifier = Modifier.padding(16.dp),
		hasStartingDivider = true
	)
}