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
import androidx.navigation.toRoute
import com.example.checkpoint.data.sampleLocalGames
import com.example.checkpoint.ui.screens.AchievementsScreen
import com.example.checkpoint.ui.screens.ExploreScreen
import com.example.checkpoint.ui.screens.GameScreen
import com.example.checkpoint.ui.screens.GamesGridScreen
import com.example.checkpoint.ui.screens.LoginScreen
import com.example.checkpoint.ui.screens.ProfileScreen
import com.example.checkpoint.ui.screens.SignUpScreen
import com.example.checkpoint.ui.theme.CheckpointTheme
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel
import com.example.checkpoint.ui.viewmodel.GameScreenViewModel
import com.example.checkpoint.ui.viewmodel.ProfileViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CheckpointTheme {
				val navController = rememberNavController()
				NavGraph(
					navController = navController, startDestination = NavigationRoute.ProfileScreen
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
	data class GameScreen(val igdbId: Int) : NavigationRoute

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
	data object SignUpScreen : NavigationRoute

	@Serializable
	data object ProfileScreen : NavigationRoute

	@Serializable
	data object AchievementsScreen : NavigationRoute

	@Serializable
	data object SettingsScreen : NavigationRoute
}

@Composable
fun NavGraph(
	navController: NavHostController,
	startDestination: NavigationRoute = NavigationRoute.ExploreScreen,
) {
	// Instantiated here in the activity scope, shared between ProfileScreen e AchievementsScreen
	val achievementsViewModel: AchievementsViewModel = viewModel()

	NavHost(
		navController = navController, startDestination = startDestination
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
			// ProfileViewModel iniettato da Koin (legge DB)
			val profileViewModel: ProfileViewModel = koinViewModel()
			ProfileScreen(
				navController = navController,
				achievementsViewModel = achievementsViewModel,
				profileViewModel = profileViewModel
			)
		}

		// Only reachable from ProfileScreen
		composable<NavigationRoute.AchievementsScreen> {
			AchievementsScreen(
				navController = navController, achievementsViewModel = achievementsViewModel
			)
		}

		composable<NavigationRoute.GameScreen> { backStackEntry ->
			val route: NavigationRoute.GameScreen = backStackEntry.toRoute()
			val vm: GameScreenViewModel = koinViewModel { parametersOf(route.igdbId, 1) }
			GameScreen(
				navController = navController, viewModel = vm
			)
		}

		composable<NavigationRoute.GamesGridScreen> {
			GamesGridScreen(
				navController, title = "Popular games", games = sampleLocalGames
			)
		}

		composable<NavigationRoute.LoginScreen> {
			LoginScreen(
				navController
			)
		}

		composable<NavigationRoute.SignUpScreen> {
			SignUpScreen(
				navController
			)
		}
	}
}