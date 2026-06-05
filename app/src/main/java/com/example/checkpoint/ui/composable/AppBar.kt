package com.example.checkpoint.ui.composable

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

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
	title: String,
	navController: NavHostController,
	searchContent: @Composable ColumnScope.() -> Unit,
	modifier: Modifier = Modifier,
	actions: @Composable () -> Unit = { },
) {
	val searchBarState = rememberSearchBarState()
	val textFieldState = rememberTextFieldState()
	val scope = rememberCoroutineScope()
	val inputField = @Composable {
		SearchBarDefaults.InputField(
			textFieldState = textFieldState,
			searchBarState = searchBarState,
			onSearch = {
				scope.launch {
					/* TODO: Run search query and save it to a list that you pass to the [searchContent] */
					searchBarState.animateToCollapsed()
				}
			},
			placeholder = {
				Text(modifier = Modifier.clearAndSetSemantics {}, text = title)
			},
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
			trailingIcon = actions,
		)
	}
	SearchBar(
		state = searchBarState,
		inputField = inputField,
		modifier = modifier
	)
	ExpandedFullScreenSearchBar(
		state = searchBarState,
		inputField = inputField
	) {
		searchContent
	}
}