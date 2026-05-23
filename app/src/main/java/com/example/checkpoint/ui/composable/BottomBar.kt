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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.NavigationRoute.ExploreScreen
import com.example.checkpoint.NavigationRoute.LibraryScreen
import com.example.checkpoint.NavigationRoute.ProfileScreen

/**
 * An item in the bottom navigation bar.
 */
enum class NavigationItem(
	val label: String,
	val route: NavigationRoute,
	val icon: ImageVector
) {
	Explore("Explore", ExploreScreen, Icons.Rounded.Home),
	Library("Library", LibraryScreen, Icons.AutoMirrored.Rounded.List),
	Profile("Profile", ProfileScreen, Icons.Rounded.Person)
}

@Composable
fun BottomBar(
	navController: NavHostController,
	selectedNavigationItem: NavigationItem
) {
	NavigationBar {
		NavigationItem.entries.forEach {
			NavigationBarItem(
				selected = it == selectedNavigationItem,
				onClick = {
					navController.navigate(it.route)
				},
				icon = {
					Icon(
						imageVector = it.icon,
						contentDescription = it.label
					)
				},
				label = { Text(it.label) }
			)
		}
	}
}