package com.example.checkpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.screens.ExploreScreen
import com.example.checkpoint.ui.screens.GameScreen
import com.example.checkpoint.ui.screens.GamesGridScreen
import com.example.checkpoint.ui.theme.CheckpointTheme
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CheckpointTheme {
				val navController = rememberNavController()
				NavGraph(
					navController = navController
				)
			}
		}
	}
}

/**
 * Represents a navigation route in the app.
 */
sealed interface NavigationRoute {
	/**
	 * Route that is presented in the bottom navigation bar.
	 */
	sealed interface BottomBarRoute : NavigationRoute {
		val icon: ImageVector
	}

	// Explore
	@Serializable
	data object ExploreScreen : BottomBarRoute {
		override val icon: ImageVector
			get() = Icons.Default.Home
	}

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
	data object LibraryScreen : BottomBarRoute {
		override val icon: ImageVector
			get() = Icons.AutoMirrored.Default.List
	}

	// Account
	@Serializable
	data object AccountScreen : BottomBarRoute {
		override val icon: ImageVector
			get() = Icons.Default.Person
	}

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
	navController: NavHostController
) {
	NavHost(
		navController = navController,
		startDestination = NavigationRoute.GameScreen
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
			ExploreScreen(navController)
			// ProfileScreen(navController)
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