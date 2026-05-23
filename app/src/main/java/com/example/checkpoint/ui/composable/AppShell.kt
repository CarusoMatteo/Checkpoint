package com.example.checkpoint.ui.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.title

import com.example.checkpoint.ui.screens.ExploreScreen
import com.example.checkpoint.ui.screens.GameScreen
import com.example.checkpoint.ui.screens.GamesGridScreen

@Composable
fun AppShell(navController: NavHostController) {
	val currentRoute: NavigationRoute =
		NavigationRoute.GameScreen // by navController.currentBackStackEntryAsState().value.

	Scaffold(
		modifier = Modifier.fillMaxSize(),
		topBar = {
			AppBar(
				title = currentRoute.title(),
				navController = navController
			)
		},
		bottomBar = {
			BottomBar(
				navController = navController,
				currentRoute = currentRoute
			)
		}
	) { innerPadding ->
		NavGraph(
			navController = navController,
			modifier = Modifier.padding(innerPadding)
		)
	}
}

@Composable
fun NavGraph(
	navController: NavHostController,
	modifier: Modifier = Modifier
) {
	NavHost(
		navController = navController,
		startDestination = NavigationRoute.GameScreen
	) {
		composable<NavigationRoute.ExploreScreen> {
			ExploreScreen(
				navController = navController,
				modifier = modifier
			)
		}
		composable<NavigationRoute.LibraryScreen> {
			ExploreScreen(
				navController,
				modifier = modifier
			)
			// LibraryScreen(navController)
		}
		composable<NavigationRoute.ProfileScreen> {
			ExploreScreen(
				navController,
				modifier = modifier
			)
			// ProfileScreen(navController)
		}

		// Temporary for testing only
		// TODO: Remove
		composable<NavigationRoute.GameScreen> {
			GameScreen(
				navController,
				modifier = modifier
			)
		}

		composable<NavigationRoute.GamesGridScreen> {
			GamesGridScreen(
				games = sampleLocalGames,
				navController,
				modifier = modifier
			)
		}
	}
}