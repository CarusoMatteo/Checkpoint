package com.example.checkpoint.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.ChipRow
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.ProfileMonogramFontSize
import com.example.checkpoint.ui.composable.ProfilePicture
import com.example.checkpoint.ui.composable.ReviewList
import com.example.checkpoint.ui.viewmodel.AchievementUiModel
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel
import com.example.checkpoint.ui.viewmodel.LibraryListUiModel
import com.example.checkpoint.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
	navController: NavHostController,
	achievementsViewModel: AchievementsViewModel, // it will be removed :<(
	profileViewModel: ProfileViewModel,
	modifier: Modifier = Modifier,
) {
	val uiState by profileViewModel.state.collectAsState()
	val scrollState = rememberScrollState()

	// I extract the data from the DB
	val currentUser = uiState.user

	val safeUser = currentUser ?: UserEntity(
		id = 0,
		username = "User",
		email = "",
		passwordHash = "",
		bio = "No biography included.",
		publicProfile = true,
		createdAt = ""
	)

	// Filter
	val achievements = uiState.achievements.filter { it.isPinned }

	// Function to navigate to the list grid
	val navigateToGrid: (String, List<Game>) -> Unit = { title, gamesList ->
		navController.currentBackStackEntry?.savedStateHandle?.set("grid_games", gamesList)
		navController.navigate(NavigationRoute.GamesGridScreen(title))
	}

	AppShell(
		navController = navController,
		title = "Welcome back, ${safeUser.username}!",
		selectedNavigationItem = NavigationItem.Profile,
		appBarActions = {
			IconButton(onClick = { /* TODO: Settings / Logout */ }) {
				Icon(Icons.Rounded.Settings, contentDescription = "Settings")
			}
		}) { innerPadding ->

		if (uiState.isLoading) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center
			) { CircularProgressIndicator() }
			return@AppShell
		}

		Column(
			modifier = modifier
				.padding(innerPadding)
				.fillMaxSize()
				.verticalScroll(scrollState)
		) {
			// ── Header
			ProfileHeader(
				user = safeUser,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp, horizontal = 16.dp)
			)

			// ── Biography
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			ProfileSection(
				title = "Biography",
				onUpdateClick = { /* TODO */ },
				modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
			)
			Text(
				text = safeUser.bio ?: "No biography included.",
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp)
			)

			// ── Favorite genres
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			ProfileSection(
				title = "Favourite Genres",
				onUpdateClick = { /* TODO */ },
				modifier = Modifier
					.padding(top = 16.dp)
					.padding(horizontal = 16.dp)
			)
			val preferredGenres = uiState.preferredGenres
			if (preferredGenres.isEmpty()) {
				Text(
					text = "No favorite genre selected.",
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
				)
			} else {
				ChipRow(
					chips = preferredGenres.map { ChipContent(label = it) },
					padding = PaddingValues(horizontal = 16.dp),
					modifier = Modifier.padding(bottom = 8.dp)
				)
			}

			// ── My Collections
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			MyCollectionsSection(
				carousels = uiState.carousels,
				modifier = Modifier.padding(vertical = 8.dp),
				onCollectionClick = { carousel ->
					navigateToGrid(carousel.listEntity.name, carousel.games)
				})

			// ── Achievement dal DB
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			AchievementsSection(
				pinnedAchievements = achievements, onSeeAllClick = {
					navController.navigate(NavigationRoute.AchievementsScreen)
				}, modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(vertical = 8.dp)
			)

			// ── Reviews from the DB
			HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			ReviewList(
				title = "Your Reviews",
				reviews = uiState.reviews,
				users = mapOf(safeUser.id to safeUser),
				modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
				hasWriteReviewButton = false
			)
		}
	}
}

// ─────────────────────────────────────────────────────────────────────────────
// Private components
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeader(user: UserEntity, modifier: Modifier = Modifier) {
	Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
		Box(contentAlignment = Alignment.BottomEnd) {
			ProfilePicture(
				user = user,
				modifier = Modifier
					.size(80.dp)
					.clip(CircleShape)
					.clickable { /* TODO: Edit profile picture */ },
				fontSize = ProfileMonogramFontSize.Profile
			)
			Icon(
				imageVector = Icons.Rounded.Edit,
				contentDescription = "Edit profile picture",
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier.size(24.dp)
			)
		}
		Spacer(Modifier.width(16.dp))
		Column {
			Text(
				text = user.username,
				style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold
			)
			Text(
				text = user.email,
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

@Composable
private fun ProfileSection(
	title: String, onUpdateClick: () -> Unit, modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(bottom = 8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold
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
}

@Composable
private fun MyCollectionsSection(
	carousels: List<LibraryListUiModel>,
	modifier: Modifier = Modifier,
	onCollectionClick: (LibraryListUiModel) -> Unit = {}
) {
	Column(modifier = modifier) {
		Text(
			text = "My Collections",
			style = MaterialTheme.typography.titleMedium,
			fontWeight = FontWeight.Bold,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
		)

		if (carousels.isEmpty()) {
			Text(
				text = "No list created.",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.padding(horizontal = 16.dp)
			)
		} else {
			LazyRow(
				horizontalArrangement = Arrangement.spacedBy(12.dp),
				contentPadding = PaddingValues(horizontal = 16.dp),
				modifier = Modifier.fillMaxWidth()
			) {
				items(carousels) { carousel ->
					val list = carousel.listEntity
					val isPrimary = list.type == "BACKLOG" || list.type == "SAVED"

					Card(
						onClick = { onCollectionClick(carousel) },
						modifier = Modifier
							.width(160.dp)
							.height(115.dp)
					) {
						Column(
							modifier = Modifier
								.fillMaxSize()
								.padding(16.dp),
							verticalArrangement = Arrangement.Center
						) {
							Icon(
								imageVector = Icons.Rounded.Folder,
								contentDescription = null,
								tint = if (isPrimary) MaterialTheme.colorScheme.primary
								else MaterialTheme.colorScheme.outline
							)
							Spacer(modifier = Modifier.height(12.dp))
							Text(
								text = list.name,
								style = MaterialTheme.typography.titleMedium,
								maxLines = 1,
								overflow = Ellipsis
							)
							Text(
								text = if (carousel.games.size == 1) "1 game" else "${carousel.games.size} games",
								style = MaterialTheme.typography.labelSmall,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun AchievementsSection(
	pinnedAchievements: List<AchievementUiModel>,
	onSeeAllClick: () -> Unit,
	modifier: Modifier = Modifier,
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
						AchievementBadge(achievement)
						Spacer(Modifier.width(12.dp))
						Column(modifier = Modifier.weight(1f)) {
							Text(
								text = achievement.name,
								style = MaterialTheme.typography.titleSmall,
								fontWeight = FontWeight.SemiBold
							)
							Text(
								text = achievement.description ?: "",
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
							Spacer(Modifier.height(4.dp))
							LinearProgressIndicator(
								progress = { achievement.progressFraction },
								modifier = Modifier
									.fillMaxWidth()
									.height(4.dp)
									.clip(MaterialTheme.shapes.small)
							)
						}
					}
				}
			}
		}
	}
}

@Composable
private fun AchievementBadge(achievement: AchievementUiModel) {
	val bgColor = if (achievement.isUnlocked) MaterialTheme.colorScheme.primaryContainer
	else MaterialTheme.colorScheme.surfaceVariant
	val textColor = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary
	else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

	Box(
		modifier = Modifier
			.size(48.dp)
			.clip(CircleShape), contentAlignment = Alignment.Center
	) {
		Canvas(modifier = Modifier.fillMaxSize()) { drawCircle(color = bgColor) }
		Text(
			text = achievement.name.first().toString(),
			style = MaterialTheme.typography.titleMedium,
			color = textColor,
			fontWeight = FontWeight.Bold
		)
	}
}