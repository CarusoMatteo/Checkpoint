package com.example.checkpoint.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.LabeledChipRow
import com.example.checkpoint.ui.composable.LabeledText
import com.example.checkpoint.ui.composable.LabeledTextWithAction
import com.example.checkpoint.ui.composable.LazyGamesCarousel
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.ReviewList
import com.example.checkpoint.ui.composable.ReviewRating
import com.example.checkpoint.ui.composable.SmallSplitButtons
import com.example.checkpoint.ui.viewmodel.GameScreenViewModel
import com.example.checkpoint.data.repositories.Game
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun GameScreen(
	navController: NavHostController,
	viewModel: GameScreenViewModel,
) {
	val state by viewModel.state.collectAsState()
	val scrollState = rememberScrollState()

	AppShell(
		navController = navController,
		title = "Game",
		selectedNavigationItem = NavigationItem.Explore,
		appBarActions = {
			IconButton(onClick = {
				if (state.isSaved) viewModel.actions.onRemoveGame()
				else viewModel.actions.onSaveGame()
			}) {
				Icon(
					imageVector = if (state.isSaved) Icons.Rounded.Notifications
					else Icons.Rounded.NotificationsNone,
					contentDescription = if (state.isSaved) "Unfollow game" else "Follow game"
				)
			}
		}) { innerPadding ->

		when {
			state.isLoading -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) { CircularProgressIndicator() }
			}

			state.error != null -> {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = state.error ?: "Errore sconosciuto",
						color = MaterialTheme.colorScheme.error
					)
				}
			}

			state.game != null -> {
				val game = state.game!!
				Column(
					modifier = Modifier
						.padding(innerPadding)
						.fillMaxSize()
						.verticalScroll(scrollState)
				) {
					GameHeader(
						game = game,
						isSaved = state.isSaved,
						averageRating = state.averageRating,
						onPrimaryClick = {
							if (state.isSaved) viewModel.actions.onRemoveGame()
							else viewModel.actions.onSaveGame()
						},
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp)
							.padding(bottom = 8.dp)
					)

					if (!game.summary.isNullOrBlank()) {
						LabeledText(
							title = "Description",
							contentText = game.summary,
							modifier = Modifier
								.padding(vertical = 8.dp, horizontal = 16.dp)
								.fillMaxWidth()
						)
					}

					// Formattazione della data e logica "Add to calendar"
					game.firstReleaseDate?.let { epochSeconds ->
						val releaseInstant = Instant.ofEpochSecond(epochSeconds)
						val localDate = releaseInstant.atZone(ZoneId.systemDefault()).toLocalDate()
						val formattedDate =
							localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))

						val now = Instant.now()
						if (releaseInstant.isAfter(now)) {
							LabeledTextWithAction(
								title = "Release date",
								contentText = formattedDate,
								actionText = "Add to calendar",
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 8.dp, horizontal = 16.dp)
							) { /* TODO: Add to calendar action */ }
						} else {
							LabeledText(
								title = "Release date",
								contentText = formattedDate,
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 8.dp, horizontal = 16.dp)
							)
						}
					}

					if (game.genres.isNotEmpty()) {
						LabeledChipRow(
							title = "Genres",
							chips = game.genres.map { ChipContent(it) },
							modifier = Modifier.padding(vertical = 8.dp)
						)
					}

					// Same Franchise Games
					if (state.franchiseGames.isNotEmpty()) {
						LazyGamesCarousel(
							title = "From the series",
							games = state.franchiseGames,
							hasStartingDivider = true,
							onGameClick = { clickedIgdbId ->
								navController.navigate(NavigationRoute.GameScreen(clickedIgdbId))
							})
					}

					// Similar games
					if (state.similarGames.isNotEmpty()) {
						LazyGamesCarousel(
							title = "Similar games",
							games = state.similarGames,
							hasStartingDivider = true,
							onGameClick = { clickedIgdbId ->
								navController.navigate(NavigationRoute.GameScreen(clickedIgdbId))
							})
					}

					// Recensioni fittizie usate temporaneamente come da file di design
					ReviewList(
						title = "Reviews",
						reviews = sampleLocalGames.first().reviews,
						modifier = Modifier.padding(horizontal = 16.dp),
						hasStartingDivider = true
					)
				}
			}
		}
	}
}

@Composable
private fun GameHeader(
	game: Game,
	isSaved: Boolean,
	averageRating: Double?,
	onPrimaryClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Row(modifier = modifier) {
		AsyncImage(
			modifier = Modifier
				.clip(MaterialTheme.shapes.extraLarge)
				.width(width = 100.dp),
			contentScale = ContentScale.FillWidth,
			model = game.coverUrl,
			contentDescription = game.name,
		)

		Column(
			modifier = Modifier.padding(start = 16.dp),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			Text(
				text = game.name,
				style = MaterialTheme.typography.headlineSmall,
				modifier = Modifier.basicMarquee()
			)
			if (game.developer != null) {
				Text(
					text = game.developer, style = MaterialTheme.typography.titleMedium
				)
			}

			// Usa la media voti reale dal DB se disponibile, altrimenti fa il fallback su IGDB
			val finalRating =
				averageRating?.toFloat() ?: (game.totalRating?.div(20))?.toFloat() ?: 0f
			ReviewRating(
				rating = finalRating, modifier = Modifier.fillMaxWidth()
			)

			SmallSplitButtons(
				onPrimaryClick = onPrimaryClick,
				onSecondaryClick = { /* TODO: dropdown menu */ },
				primaryIcon = {
					Icon(
						imageVector = Icons.Rounded.AddCircleOutline, contentDescription = null
					)
				},
				primaryLabel = if (isSaved) "Saved" else "Add to Backlog",
				secondaryIcon = {
					Icon(
						imageVector = Icons.Rounded.KeyboardArrowDown, contentDescription = null
					)
				})
		}
	}
}