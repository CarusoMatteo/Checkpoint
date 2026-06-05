package com.example.checkpoint.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.SearchAppShell

@Composable
fun SearchScreen(
	navController: NavHostController,
) {
	SearchAppShell(
		navController,
		title = "Explore games",
		selectedNavigationItem = NavigationItem.Explore,
		appBarActions = {
			Icon(
				imageVector = Icons.Rounded.Search,
				contentDescription = "Search"
			)
		},
		mainContent = { innerPadding ->
		},
		searchContent = {
			GamesGrid(
				games = listOf(),
				navController = navController,
			)
		}
	)
}