package com.example.checkpoint.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.Achievement
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.UserProfile
import com.example.checkpoint.data.sampleUserProfile
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.LabeledChipRow
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.ProfileMonogram
import com.example.checkpoint.ui.composable.ReviewList
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel

@Composable
fun ProfileScreen(
	navController: NavHostController,
	achievementsViewModel: AchievementsViewModel,
	modifier: Modifier = Modifier,
	profile: UserProfile = sampleUserProfile
) {
	val pinned = achievementsViewModel.pinnedAchievements

	AppShell(
		navController = navController,
		title = "Welcome back!",
		selectedNavigationItem = NavigationItem.Profile,
		appBarActions = {
			IconButton(onClick = { /* TODO: Settings */ }) {
				Icon(Icons.Outlined.Settings, contentDescription = "Settings")
			}
		}) { innerPadding ->
		LazyColumn(
			modifier = modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			item {
				ProfileHeader(
					profile = profile, modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp, horizontal = 16.dp)
				)
			}
			item {
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
				ProfileSection(
					title = "Biography",
					onUpdateClick = { /* TODO */ },
					modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
				) {
					Text(
						text = profile.bio,
						style = MaterialTheme.typography.bodyMedium,
						textAlign = TextAlign.Justify
					)
				}
			}

			item {
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
				ProfileSection(
					title = "Favourite Genres",
					onUpdateClick = { /* TODO */ },
					modifier = Modifier.padding(vertical = 16.dp)
				) {
					LabeledChipRow(
						title = "Favourite Genres",
						chips = profile.preferredGenres.map { ChipContent(label = it) },
						padding = PaddingValues(horizontal = 16.dp)
					)
				}
			}

			item {
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
				AchievementsSection(
					pinnedAchievements = pinned, onSeeAllClick = {
						navController.navigate(NavigationRoute.AchievementsScreen)
					},
					modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
				)
			}

			item {
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
				ReviewList(
					title = "Your Reviews",
					reviews = profile.reviews,
					modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
					hasWriteReviewButton = false
				)
			}
		}
	}
}

@Composable
private fun ProfileHeader(profile: UserProfile, modifier: Modifier = Modifier) {
	Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
		Box(contentAlignment = Alignment.BottomEnd) {
			if (profile.user.profilePicture != null) {
				AsyncImage(
					model = profile.user.profilePicture,
					contentDescription = "Avatar",
					contentScale = ContentScale.Crop,
					modifier = Modifier
						.size(80.dp)
						.clip(CircleShape)
				)
			} else {
				ProfileMonogram(
					letter = profile.user.name.first(), modifier = Modifier.size(80.dp)
				)
			}
			Box(
				modifier = Modifier
					.size(24.dp)
					.clip(CircleShape)
					.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
				contentAlignment = Alignment.Center
			) {
				Icon(
					imageVector = Icons.Rounded.Edit,
					contentDescription = "Edit avatar",
					modifier = Modifier.size(14.dp),
					tint = MaterialTheme.colorScheme.onSurface
				)
			}
		}

		Spacer(Modifier.width(16.dp))

		Column {
			Text(
				text = profile.user.name,
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.Bold
			)
			Text(
				text = profile.email,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

@Composable
private fun ProfileSection(
	title: String,
	onUpdateClick: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit
) {
	Column(modifier = modifier) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			FilledTonalButton(onClick = onUpdateClick) {
				Icon(
					imageVector = Icons.Rounded.Edit,
					contentDescription = null,
					modifier = Modifier.size(16.dp)
				)
				Spacer(Modifier.width(4.dp))
				Text("Update")
			}
		}
		Spacer(Modifier.height(8.dp))
		content()
	}
}

@Composable
private fun AchievementsSection(
	pinnedAchievements: List<Achievement>, onSeeAllClick: () -> Unit, modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = "Achievements",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			IconButton(onClick = onSeeAllClick) {
				Icon(
					imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
					contentDescription = "See all achievements"
				)
			}
		}
		Spacer(Modifier.height(4.dp))

		if (pinnedAchievements.isEmpty()) {
			Text(
				text = "Complete and pin up to three achievements to display them here!",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		} else {
			Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
				pinnedAchievements.forEach { achievement ->
					Row(verticalAlignment = Alignment.CenterVertically) {
						ProfileMonogram(letter = achievement.name.first())
						Spacer(Modifier.width(12.dp))
						Column {
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
						}
					}
				}
			}
		}
	}
}