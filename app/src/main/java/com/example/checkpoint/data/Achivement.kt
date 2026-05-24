package com.example.checkpoint.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

data class Achievement(
	val id: Int,
	val name: String,
	val description: String,
	val icon: ImageVector,
	val progress: Int,
	val threshold: Int
) {
	val isUnlocked: Boolean get() = progress >= threshold
	val progressFraction: Float get() = (progress.toFloat() / threshold).coerceIn(0f, 1f)
}

val sampleAchievements = listOf(
	Achievement(
		id = 1,
		name = "Multiplatform enjoyer",
		description = "Try different platforms",
		icon = Icons.Rounded.Devices,
		progress = 5,
		threshold = 10
	),
	Achievement(
		id = 2,
		name = "Lots to look forward to",
		description = "Enable notifications for upcoming games",
		icon = Icons.Rounded.Notifications,
		progress = 15,
		threshold = 15
	),
	Achievement(
		id = 3,
		name = "Variety Gamer",
		description = "Play games from different genres",
		icon = Icons.Rounded.Category,
		progress = 0,
		threshold = 5
	),
	Achievement(
		id = 4,
		name = "Game Finisher",
		description = "Finish 10 games",
		icon = Icons.Rounded.SportsEsports,
		progress = 7,
		threshold = 10
	),
	Achievement(
		id = 5,
		name = "Critic",
		description = "Write 5 reviews",
		icon = Icons.Rounded.RateReview,
		progress = 2,
		threshold = 5
	),
	Achievement(
		id = 6,
		name = "Consistent Player",
		description = "Play 20 games of the same genre",
		icon = Icons.Rounded.TrendingUp,
		progress = 3,
		threshold = 20
	)
)