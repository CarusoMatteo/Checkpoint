package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.ClickActions
import com.example.checkpoint.ui.composable.GameCover
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.viewmodel.LibraryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GamesGridScreen(
	navController: NavHostController,
	title: String,
	games: List<Game>,
	modifier: Modifier = Modifier,
) {
	// 1. Recuperiamo l'entry dello schermo precedente nella navigazione (la LibraryScreen)
	val previousEntry = remember(navController) { navController.previousBackStackEntry }

	// 2. Diciamo a Koin di usare il ViewModel dello schermo precedente (se esiste), condividendo l'istanza
	val libraryVm: LibraryViewModel = if (previousEntry != null) {
		koinViewModel(viewModelStoreOwner = previousEntry)
	} else {
		koinViewModel()
	}

	// 3. Recuperiamo l'ID della lista dal savedStateHandle del livello precedente
	val listId = remember {
		previousEntry?.savedStateHandle?.get<Int>("grid_list_id")
	}

	val libraryState by libraryVm.state.collectAsStateWithLifecycle()
	var gameToRemove by remember { mutableStateOf<Game?>(null) }

	val displayGames = if (listId != null) {
		libraryState.carousels.find { it.listEntity.id == listId }?.games ?: games
	} else {
		games
	}

	AppShell(
		navController, title = title, selectedNavigationItem = NavigationItem.Explore
	) { innerPadding ->
		LazyVerticalGrid(
			columns = GridCells.Fixed(2),
			contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			items(displayGames, key = { it.igdbId }) { game ->
				GameCover(
					game = game,
					showInformationOverlay = true,
					clickActions = ClickActions(
						onClick = {
							navController.navigate(NavigationRoute.GameScreen(game.igdbId))
						},
						onLongClick = {
							if (listId != null) {
								gameToRemove = game
							}
						}),
					modifier = Modifier.fillMaxWidth()
				)
			}
		}
	}

	if (gameToRemove != null) {
		val game = gameToRemove!!
		AlertDialog(
			onDismissRequest = { gameToRemove = null },
			title = { Text(text = game.name) },
			text = { Text("Do you really want to remove this game from the list?") },
			confirmButton = {
				TextButton(
					colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
					onClick = {
						if (listId != null) {
							// Modificando questo ViewModel condiviso, cambiamo istantaneamente lo stato di LibraryScreen
							libraryVm.removeGameFromListByIgdbId(listId, game.igdbId)
						}
						gameToRemove = null
					}
				) { Text("Remove") }
			},
			dismissButton = {
				TextButton(onClick = { gameToRemove = null }) { Text("Cancel") }
			}
		)
	}
}