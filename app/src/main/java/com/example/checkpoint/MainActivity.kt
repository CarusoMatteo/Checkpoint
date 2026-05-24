package com.example.checkpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.screens.AchievementsScreen
import com.example.checkpoint.ui.screens.ExploreScreen
import com.example.checkpoint.ui.screens.GameScreen
import com.example.checkpoint.ui.screens.GamesGridScreen
import com.example.checkpoint.ui.screens.ProfileScreen
import com.example.checkpoint.ui.theme.CheckpointTheme
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CheckpointTheme {
				val navController = rememberNavController()
				NavGraph(
					navController = navController,
					startDestination = NavigationRoute.ProfileScreen
				)
			}
		}
	}
}

/**
 * Represents a navigation route in the app.
 */
sealed interface NavigationRoute {
	@Serializable
	data object ExploreScreen : NavigationRoute

	@Serializable
	data object GamesGridScreen : NavigationRoute

	@Serializable
	data object SearchScreen : NavigationRoute

	// Single items
	@Serializable
	data object GameScreen : NavigationRoute

	@Serializable
	data object UserScreen : NavigationRoute

	// Library
	@Serializable
	data object LibraryScreen : NavigationRoute

	// Account
	@Serializable
	data object AccountScreen : NavigationRoute

	@Serializable
	data object LoginScreen : NavigationRoute

	@Serializable
	data object RegisterScreen : NavigationRoute

	@Serializable
	data object ProfileScreen : NavigationRoute

	@Serializable
	data object AchievementsScreen : NavigationRoute
}

@Composable
fun NavGraph(
	navController: NavHostController,
	startDestination: NavigationRoute = NavigationRoute.ExploreScreen,
) {
	// Istanziato qui lo scope all'Activity, condiviso tra ProfileScreen e AchievementsScreen
	val achievementsViewModel: AchievementsViewModel = viewModel()

	NavHost(
		navController = navController,
		startDestination = startDestination
	) {
		composable<NavigationRoute.ExploreScreen> {
			ExploreScreen(navController)
		}
		// TODO: Implement these screens
		composable<NavigationRoute.LibraryScreen> {
			ExploreScreen(navController)
			// LibraryScreen(navController)
		}
		composable<NavigationRoute.ProfileScreen> {
			//ExploreScreen(navController)
			ProfileScreen(navController, achievementsViewModel = achievementsViewModel)
		}
		//raggiungibile solo da ProfileScreen
		composable<NavigationRoute.AchievementsScreen> {
			AchievementsScreen(
				navController = navController,
				achievementsViewModel = achievementsViewModel
			)
		}

		// TODO: Remove, these are temporary for testing only
		composable<NavigationRoute.GameScreen> {
			GameScreen(
				navController,
				game = sampleLocalGames.first()
			)
		}

		composable<NavigationRoute.GamesGridScreen> {
			GamesGridScreen(
				navController,
				title = "Popular games",
				games = sampleLocalGames
			)
		}
	}
}