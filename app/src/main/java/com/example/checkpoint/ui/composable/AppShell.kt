package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppShell(
	navController: NavHostController,
	title: String,
	selectedNavigationItem: NavigationItem,
	appBarActions: @Composable () -> Unit = { },
	mainContent: @Composable (PaddingValues) -> Unit,
	searchContent: @Composable ColumnScope.() -> Unit
) {
	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {

		},
		bottomBar = {
			BottomBar(
				navController = navController,
				selectedNavigationItem = selectedNavigationItem
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			SearchAppBar(
				title = title,
				navController = navController,
				actions = appBarActions,
				searchContent = searchContent,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
			)
			mainContent(innerPadding)
		}
	}
}