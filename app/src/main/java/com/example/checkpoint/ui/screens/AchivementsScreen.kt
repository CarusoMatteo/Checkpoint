package com.example.checkpoint.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.checkpoint.data.Achievement
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel

@Composable
fun AchievementsScreen(
	navController: NavHostController,
	achievementsViewModel: AchievementsViewModel,
	modifier: Modifier = Modifier
) {
	val pinnedIds by achievementsViewModel.pinnedIds.collectAsState()

	AppShell(
		navController = navController,
		title = "Achievements",
		selectedNavigationItem = NavigationItem.Profile
	) { innerPadding ->
		Column(
			modifier = modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			Text(
				text = "Complete and pin up to three achievements to display them in your profile!",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			Text(
				text = "Pinned: ${pinnedIds.size} / 3",
				style = MaterialTheme.typography.labelMedium,
				fontWeight = FontWeight.SemiBold,
				color = if (pinnedIds.size == 3) MaterialTheme.colorScheme.primary
				else MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
			)

			HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

			LazyColumn {
				items(achievementsViewModel.allAchievements, key = { it.id }) { achievement ->
					AchievementRow(
						achievement = achievement,
						isPinned = achievement.id in pinnedIds,
						canPin = achievement.isUnlocked && (achievement.id in pinnedIds || pinnedIds.size < 3),
						onTogglePin = { achievementsViewModel.togglePin(achievement.id) })
					HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
				}
			}
		}
	}
}

@Composable
private fun AchievementRow(
	achievement: Achievement, isPinned: Boolean, canPin: Boolean, onTogglePin: () -> Unit
) {
	val pinBg by animateColorAsState(
		targetValue = if (isPinned) MaterialTheme.colorScheme.primary else Color.Transparent,
		label = "pin_bg"
	)
	val pinTint by animateColorAsState(
		targetValue = when {
			isPinned -> MaterialTheme.colorScheme.onPrimary
			canPin -> MaterialTheme.colorScheme.onSurfaceVariant
			else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
		}, label = "pin_tint"
	)

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 12.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		AchievementBadge(achievement = achievement)
		Spacer(Modifier.width(12.dp))

		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = achievement.name,
				style = MaterialTheme.typography.titleSmall,
				fontWeight = FontWeight.SemiBold
			)
			Text(
				text = achievement.description,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Spacer(Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				Text(
					text = "${achievement.progress}",
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				LinearProgressIndicator(
					progress = { achievement.progressFraction },
					modifier = Modifier
						.weight(1f)
						.height(4.dp)
						.clip(MaterialTheme.shapes.small),
					color = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary
					else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
					trackColor = MaterialTheme.colorScheme.surfaceVariant
				)
				Text(
					text = "${achievement.threshold}",
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}

		Spacer(Modifier.width(8.dp))

		IconButton(
			onClick = onTogglePin,
			enabled = canPin,
			modifier = Modifier
				.size(40.dp)
				.clip(CircleShape)
				.background(pinBg)
				.then(
					if (!isPinned && canPin) Modifier.border(
						1.dp, MaterialTheme.colorScheme.outline, CircleShape
					)
					else Modifier
				)
		) {
			Icon(
				imageVector = Icons.Rounded.PushPin,
				contentDescription = if (isPinned) "Rimuovi pin" else "Pinna achievement",
				tint = pinTint,
				modifier = Modifier.size(20.dp)
			)
		}
	}
}

@Composable
fun AchievementBadge(achievement: Achievement) {
	val bgColor = if (achievement.isUnlocked) MaterialTheme.colorScheme.primaryContainer
	else MaterialTheme.colorScheme.surfaceVariant

	val iconTint = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary
	else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

	Box(
		modifier = Modifier
			.size(48.dp)
			.clip(CircleShape)
			.background(bgColor),
		contentAlignment = Alignment.Center
	) {
		Icon(
			imageVector = achievement.icon,
			contentDescription = null,
			tint = iconTint,
			modifier = Modifier.size(26.dp)
		)
	}
}