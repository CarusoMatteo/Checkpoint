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
import com.example.checkpoint.data.ChipContent
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.LabeledChipRow
import com.example.checkpoint.ui.composable.LabeledText
import com.example.checkpoint.ui.composable.LazyGamesCarousel
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.ReviewRating
import com.example.checkpoint.ui.composable.SmallSplitButtons
import com.example.checkpoint.ui.viewmodel.GameScreenViewModel

@Composable
fun GameScreen(
	navController: NavHostController,
	viewModel: GameScreenViewModel,
) {
	val state by viewModel.state.collectAsState()
	val scrollState = rememberScrollState()

	AppShell(
		navController = navController,
		title = state.game?.name ?: "Game",
		selectedNavigationItem = NavigationItem.Explore,
		appBarActions = {
			IconButton(onClick = {
				if (state.isSaved) viewModel.actions.onRemoveGame()
				else viewModel.actions.onSaveGame()
			}) {
				Icon(
					imageVector = if (state.isSaved) Icons.Rounded.Notifications
					else Icons.Rounded.NotificationsNone,
					contentDescription = if (state.isSaved) "Rimuovi dai salvati" else "Salva gioco"
				)
			}
		}
	) { innerPadding ->

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
					// ── Header ──────────────────────────────────────────────
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp, vertical = 8.dp)
					) {
						AsyncImage(
							model = game.coverUrl,
							contentDescription = game.name,
							modifier = Modifier
								.clip(MaterialTheme.shapes.extraLarge)
								.width(100.dp),
							contentScale = ContentScale.FillWidth,
						)
						Column(
							modifier = Modifier.padding(start = 16.dp),
							verticalArrangement = Arrangement.spacedBy(4.dp)
						) {
							Text(
								text = game.name,
								style = MaterialTheme.typography.headlineSmall,
								modifier = Modifier.basicMarquee()
							)
							if (game.developer != null) {
								Text(
									text = game.developer,
									style = MaterialTheme.typography.titleMedium
								)
							}
							ReviewRating(
								rating = (game.totalRating?.div(20))?.toFloat() ?: 0f,
								modifier = Modifier.fillMaxWidth()
							)
							SmallSplitButtons(
								onPrimaryClick = {
									if (state.isSaved) viewModel.actions.onRemoveGame()
									else viewModel.actions.onSaveGame()
								},
								onSecondaryClick = { /* TODO: dropdown opzioni log */ },
								primaryIcon = {
									Icon(
										imageVector = Icons.Rounded.AddCircleOutline,
										contentDescription = null
									)
								},
								primaryLabel = if (state.isSaved) "Salvato" else "Aggiungi",
								secondaryIcon = {
									Icon(
										imageVector = Icons.Rounded.KeyboardArrowDown,
										contentDescription = null
									)
								}
							)
						}
					}

					// ── Descrizione ─────────────────────────────────────────
					if (!game.summary.isNullOrBlank()) {
						LabeledText(
							title = "Description",
							contentText = game.summary,
							modifier = Modifier
								.padding(horizontal = 16.dp, vertical = 8.dp)
								.fillMaxWidth()
						)
					}

					// ── Generi ──────────────────────────────────────────────
					if (game.genres.isNotEmpty()) {
						LabeledChipRow(
							title = "Genres",
							chips = game.genres.map { ChipContent(it) },
							modifier = Modifier.padding(vertical = 8.dp)
						)
					}

					// ── Piattaforme ─────────────────────────────────────────
					if (game.platforms.isNotEmpty()) {
						LabeledChipRow(
							title = "Platforms",
							chips = game.platforms.map { ChipContent(it) },
							modifier = Modifier.padding(vertical = 8.dp)
						)
					}

					// ── Giochi correlati (ancora sample) ────────────────────
					LazyGamesCarousel(
						title = "You might also like",
						games = sampleLocalGames,
						hasStartingDivider = true
					)

					// TODO: sostituire ReviewList con ReviewEntity dal ViewModel
					// quando la schermata di scrittura recensione sarà implementata
				}
			}
		}
	}
}
