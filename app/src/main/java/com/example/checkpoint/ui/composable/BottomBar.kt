package com.example.checkpoint.ui.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute

@Composable
fun BottomBar(
	navController: NavHostController,
	currentRoute: NavigationRoute?
) {

	NavigationBar {
		NavigationBarItem(
			selected = false,
			onClick = {
				navController.navigate(NavigationRoute.ExploreScreen)
			},
			icon = {
				Icon(
					imageVector = Icons.Rounded.Home,
					contentDescription = "Explore"
				)
			},
			label = { Text("Explore") }
		)

		NavigationBarItem(
			selected = false,
			onClick = {
				navController.navigate(NavigationRoute.LibraryScreen)
			},
			icon = {
				Icon(
					imageVector = Icons.AutoMirrored.Rounded.List,
					contentDescription = "Library"
				)
			},
			label = { Text("Library") }
		)

		NavigationBarItem(
			selected = false,
			onClick = {},
			icon = {
				Icon(
					imageVector = Icons.Rounded.Person,
					contentDescription = "Profile"
				)
			},
			label = { Text("Profile") }
		)
	}
}