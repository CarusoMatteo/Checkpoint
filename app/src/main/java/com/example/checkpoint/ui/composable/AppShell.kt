package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppShell(
	navController: NavHostController,
	title: String,
	selectedNavigationItem: NavigationItem,
	mainContent: @Composable () -> Unit,
	searchContent: @Composable ColumnScope.() -> Unit,
	actions: @Composable () -> Unit,
	onSearch: (String) -> Unit
) {
	val searchBarState = rememberSearchBarState()
	val textFieldState = rememberTextFieldState()

	val inputField = @Composable {
		SearchBarDefaults.InputField(
			textFieldState = textFieldState,
			searchBarState = searchBarState,
			onSearch = onSearch,
			placeholder = { Text(title) },
			leadingIcon = {
				if (navController.previousBackStackEntry != null) {
					IconButton(onClick = { navController.navigateUp() }) {
						Icon(
							imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
							contentDescription = "Back"
						)
					}
				}
			},
			trailingIcon = actions
		)
	}

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			SearchAppBar(
				searchBarState = searchBarState,
				inputField = inputField
			)
		},
		bottomBar = {
			BottomBar(
				navController = navController,
				selectedNavigationItem = selectedNavigationItem
			)
		}
	) { innerPadding ->
		ExpandedFullScreenSearchBar(
			state = searchBarState,
			inputField = inputField,
			content = searchContent
		)
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
		) {
			mainContent()
		}
	}
}