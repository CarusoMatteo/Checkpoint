package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.ChipRow
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.ReviewList
import com.example.checkpoint.ui.viewmodel.UserViewModel

@Composable
fun UserScreen(
	navController: NavHostController,
	userViewModel: UserViewModel,
	modifier: Modifier = Modifier,
) {
	val uiState by userViewModel.state.collectAsState()
	val scrollState = rememberScrollState()

	val safeUser = uiState.user ?: UserEntity(
		id = 0,
		username = "User",
		email = "",
		passwordHash = "",
		bio = "No biography included.",
		publicProfile = true,
		createdAt = ""
	)

	val pinnedAchievements = uiState.achievements.filter { it.isPinned }

	val navigateToGrid: (String, List<Game>) -> Unit = { title, gamesList ->
		navController.currentBackStackEntry?.savedStateHandle?.set("grid_games", gamesList)
		navController.navigate(NavigationRoute.GamesGridScreen(title))
	}

	AppShell(
		navController = navController,
		title = safeUser.username,
		selectedNavigationItem = NavigationItem.Explore,
	) { innerPadding ->

		when {
			uiState.isLoading -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) { CircularProgressIndicator() }
			}

			uiState.isPrivate -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) {
					Column(
						horizontalAlignment = Alignment.CenterHorizontally,
						verticalArrangement = Arrangement.spacedBy(8.dp)
					) {
						Text(
							text = "This profile is private",
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.Bold
						)
						Text(
							text = "The user has chosen to keep their profile private.",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}

			else -> {
				Column(
					modifier = modifier
						.padding(innerPadding)
						.fillMaxSize()
						.verticalScroll(scrollState)
				) {
					// ── Header (read-only, no avatar click)
					ProfileHeader(
						user = safeUser,
						onAvatarClick = null,
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 8.dp, horizontal = 16.dp)
					)

					// ── Biography (read-only)
					HorizontalDivider(Modifier.padding(horizontal = 16.dp))
					Text(
						text = "Biography",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold,
						modifier = Modifier
							.padding(top = 16.dp, bottom = 8.dp)
							.padding(horizontal = 16.dp)
					)
					Text(
						text = safeUser.bio ?: "No biography included.",
						style = MaterialTheme.typography.bodyMedium,
						modifier = Modifier
							.padding(horizontal = 16.dp)
							.padding(bottom = 8.dp)
					)

					// ── Favorite genres (read-only)
					HorizontalDivider(Modifier.padding(horizontal = 16.dp))
					Text(
						text = "Favourite Genres",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold,
						modifier = Modifier
							.padding(top = 16.dp)
							.padding(horizontal = 16.dp)
					)
					if (uiState.preferredGenres.isEmpty()) {
						Text(
							text = "No favorite genre selected.",
							style = MaterialTheme.typography.bodyMedium,
							color = MaterialTheme.colorScheme.onSurfaceVariant,
							modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
						)
					} else {
						ChipRow(
							chips = uiState.preferredGenres.map { ChipContent(label = it) },
							padding = PaddingValues(horizontal = 16.dp),
							modifier = Modifier.padding(bottom = 8.dp)
						)
					}

					// ── Collections (read-only)
					HorizontalDivider(Modifier.padding(horizontal = 16.dp))
					MyCollectionsSection(
						carousels = uiState.carousels,
						modifier = Modifier.padding(vertical = 8.dp),
						onCollectionClick = { carousel ->
							navigateToGrid(carousel.listEntity.name, carousel.games)
						})

					// ── Achievements (read-only)
					HorizontalDivider(Modifier.padding(horizontal = 16.dp))
					AchievementsSection(
						pinnedAchievements = pinnedAchievements,
						onSeeAllClick = null,
						modifier = Modifier
							.padding(horizontal = 16.dp)
							.padding(vertical = 8.dp)
					)

					// ── Reviews — click opens the GameScreen of the reviewed game
					HorizontalDivider(Modifier.padding(horizontal = 16.dp))
					ReviewList(
						title = "${safeUser.username}'s Reviews",
						reviews = uiState.reviews,
						users = uiState.user?.let { mapOf(it.id to it) } ?: emptyMap(),
						modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
						hasWriteReviewButton = false,
						onReviewClick = { review ->
							val igdbId = uiState.igdbIdByGameId[review.gameId]
							if (igdbId != null) navController.navigate(
								NavigationRoute.GameScreen(
									igdbId
								)
							)
						}
					)
				}
			}
		}
	}
}