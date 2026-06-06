package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.SearchAppShell
import com.example.checkpoint.ui.composable.SearchResultList

@Composable
fun SearchScreen(
	navController: NavHostController,
) {
	val sampleGames = listOf(
		Game(
			id = 0,
			igdbId = 123,
			name = "Example Game",
			summary = "This is an example game.",
			coverUrl = null,
			genres = listOf("Action", "Adventure"),
			platforms = listOf("PC", "PlayStation 5"),
			developer = "Example Studios",
			publisher = "Example Publishing",
			firstReleaseDate = 1700000000000,
			totalRating = 85.5,
			totalRatingCount = 1000
		), Game(
			id = 0,
			igdbId = 123,
			name = "Example Game",
			summary = "This is an example game.",
			coverUrl = null,
			genres = listOf("Action", "Adventure"),
			platforms = listOf("PC", "PlayStation 5"),
			developer = "Example Studios",
			publisher = "Example Publishing",
			firstReleaseDate = 1700000000000,
			totalRating = 85.5,
			totalRatingCount = 1000
		)
	)

	SearchAppShell(
		navController,
		title = "Explore games",
		selectedNavigationItem = NavigationItem.Explore,
		mainContent = {
			Text(
				text = "MainContent text",
				modifier = Modifier.padding(horizontal = 16.dp)
			)
		},
		searchContent = {
			SearchResultList(
				games = sampleGames // TODO: Replace with actual search results
			)
		},
		actions = {
			Row {
				IconButton(
					onClick = { /* TODO: Open filter dialog */ }
				) {
					Icon(
						imageVector = Icons.Rounded.FilterAlt,
						contentDescription = "Show filters"
					)
				}
				IconButton(
					onClick = { /* TODO: Search */ }
				) {
					Icon(
						imageVector = Icons.Rounded.Search,
						contentDescription = null
					)
				}
			}
		},
		onSearch = { /* TODO: Implement search feature to update displayed games */ }
	)
}