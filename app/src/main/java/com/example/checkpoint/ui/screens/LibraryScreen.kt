package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.LazyGamesCarousel
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.viewmodel.LibraryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryScreen(
	navController: NavHostController,
	vm: LibraryViewModel = koinViewModel()
) {
	val state by vm.state.collectAsStateWithLifecycle()
	val scrollState = rememberScrollState()

	var showAddListDialog by remember { mutableStateOf(false) }
	var newListName by remember { mutableStateOf("") }

	// Stati per le finestre di conferma
	var selectedGameContext by remember { mutableStateOf<Pair<Int, Game>?>(null) } // (List ID, Game)
	var listToDelete by remember { mutableStateOf<Int?>(null) } // Contiene l'ID della lista da eliminare

	val navigateToGrid: (String, List<Game>) -> Unit = { title, gamesList ->
		navController.currentBackStackEntry?.savedStateHandle?.set("grid_games", gamesList)
		navController.navigate(NavigationRoute.GamesGridScreen(title))
	}

	AppShell(
		navController = navController,
		title = "Library",
		selectedNavigationItem = NavigationItem.Library
	) { innerPadding ->

		Box(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			if (state.isLoading) {
				CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
			} else if (state.errorMessage != null) {
				Text(
					text = state.errorMessage!!,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.align(Alignment.Center)
				)
			} else {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.verticalScroll(scrollState)
						.padding(bottom = 80.dp) // Spazio per non coprire i caroselli con il FAB
				) {
					if (state.carousels.isEmpty()) {
						Box(
							modifier = Modifier
								.fillMaxSize()
								.padding(32.dp),
							contentAlignment = Alignment.Center
						) {
							Text("Non hai ancora creato nessuna lista o le tue liste sono vuote.")
						}
					} else {
						state.carousels.forEachIndexed { index, carouselModel ->

							val isCustomList = carouselModel.listEntity.type == "CUSTOM"

							LazyGamesCarousel(
								title = carouselModel.listEntity.name,
								games = carouselModel.games,
								hasStartingDivider = index > 0,
								hasDeleteAction = isCustomList,
								onDeleteClick = {
									// Invece di eliminare direttamente, apriamo il dialog di conferma
									listToDelete = carouselModel.listEntity.id
								},
								onGameClick = { igdbId ->
									navController.navigate(NavigationRoute.GameScreen(igdbId))
								},
								onGameLongClick = { game ->
									selectedGameContext = Pair(carouselModel.listEntity.id, game)
								},
								onSeeAllClick = {
									navigateToGrid(
										carouselModel.listEntity.name,
										carouselModel.games
									)
								}
							)
						}
					}
				}
			}

			// FAB per creare una nuova lista
			FloatingActionButton(
				onClick = { showAddListDialog = true },
				containerColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.primary,
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.padding(16.dp)
			) {
				Icon(imageVector = Icons.Rounded.Add, contentDescription = "Nuova Lista")
			}
		}

		// ───────────────────────────────────────────────────────────────────────
		// DIALOGS
		// ───────────────────────────────────────────────────────────────────────

		// 1. Dialog per creare una nuova lista
		if (showAddListDialog) {
			AlertDialog(
				onDismissRequest = { showAddListDialog = false },
				title = { Text(text = "New List") },
				text = {
					OutlinedTextField(
						value = newListName,
						onValueChange = { newListName = it },
						label = { Text("Name") },
						singleLine = true,
						modifier = Modifier.fillMaxWidth()
					)
				},
				confirmButton = {
					Button(
						onClick = {
							if (newListName.isNotBlank()) {
								vm.createCustomList(newListName)
								newListName = ""
								showAddListDialog = false
							}
						}
					) { Text("Create") }
				},
				dismissButton = {
					TextButton(onClick = { showAddListDialog = false }) { Text("Cancel") }
				}
			)
		}

		// 2. Dialog per rimuovere un gioco dalla lista
		selectedGameContext?.let { context ->
			val listId = context.first
			val game = context.second

			AlertDialog(
				onDismissRequest = { selectedGameContext = null },
				title = { Text(text = game.name) },
				text = { Text("Vuoi davvero rimuovere questo gioco dalla lista?") },
				confirmButton = {
					TextButton(
						colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
						onClick = {
							vm.removeGameFromListByIgdbId(listId, game.igdbId)
							selectedGameContext = null
						}
					) { Text("Rimuovi") }
				},
				dismissButton = {
					TextButton(onClick = { selectedGameContext = null }) { Text("Annulla") }
				}
			)
		}

		// 3. NUOVO Dialog per eliminare una Custom List
		listToDelete?.let { listId ->
			AlertDialog(
				onDismissRequest = { listToDelete = null },
				title = { Text("Elimina Lista") },
				text = { Text("Sei sicuro di voler eliminare questa lista? Tutti i giochi al suo interno verranno rimossi. L'azione è irreversibile.") },
				confirmButton = {
					TextButton(
						colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
						onClick = {
							vm.deleteCustomList(listId)
							listToDelete = null // Chiude il dialog
						}
					) { Text("Elimina") }
				},
				dismissButton = {
					TextButton(onClick = { listToDelete = null }) { Text("Annulla") }
				}
			)
		}
	}
}