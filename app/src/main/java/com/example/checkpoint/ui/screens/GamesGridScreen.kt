package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.ClickActions
import com.example.checkpoint.ui.composable.GameCover
import com.example.checkpoint.ui.composable.NavigationItem

@Composable
fun GamesGridScreen(
	navController: NavHostController,
	title: String,
	games: List<Game>,
	modifier: Modifier = Modifier
) {
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
			items(games, key = { it.igdbId }) { game ->
				GameCover(
					game = game,
					showInformationOverlay = true,
					clickActions = ClickActions(
						onClick = {
							navController.navigate(NavigationRoute.GameScreen(game.igdbId))
						},
						onLongClick = { /* TODO: May open up Multiple choice window */ }),
					modifier = Modifier.fillMaxWidth()
				)
			}
		}
	}
}