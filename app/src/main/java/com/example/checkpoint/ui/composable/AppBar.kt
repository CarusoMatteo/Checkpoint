package com.example.checkpoint.ui.composable

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopSearchBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
	title: String,
	navController: NavHostController,
	modifier: Modifier = Modifier,
	actions: @Composable RowScope.() -> Unit = { }
) {
	TopAppBar(
		title = {
			Text(
				text = title,
				modifier = Modifier.basicMarquee()
			)
		},
		modifier = modifier,
		navigationIcon = {
			if (navController.previousBackStackEntry != null) {
				IconButton(onClick = { navController.navigateUp() }) {
					Icon(
						imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
						contentDescription = "Back"
					)
				}
			}
		},
		actions = actions
	)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
	inputField: @Composable () -> Unit,
	searchBarState: SearchBarState
) {
	TopSearchBar(
		state = searchBarState,
		inputField = inputField
	)
}