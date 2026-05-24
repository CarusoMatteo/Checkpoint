package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

/**
 * Composable that contains the Scaffold, AppBar and BottomBar.
 * Should be used as the root composable for each screen in the app.
 */
@Composable
fun AppShell(
	navController: NavHostController,
	title: String,
	selectedNavigationItem: NavigationItem,
	appBarActions: @Composable RowScope.() -> Unit = { },
	content: @Composable (PaddingValues) -> Unit
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			AppBar(
				title = title,
				navController = navController,
				actions = appBarActions
			)
		},
		bottomBar = {
			BottomBar(
				navController = navController,
				selectedNavigationItem = selectedNavigationItem
			)
		}
	) { innerPadding ->
		content(innerPadding)
	}
}