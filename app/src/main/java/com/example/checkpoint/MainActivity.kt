package com.example.checkpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.checkpoint.data.repositories.AuthRepository
import com.example.checkpoint.data.repositories.Game
import com.example.checkpoint.data.repositories.UiTheme
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.data.session.SessionState
import com.example.checkpoint.ui.screens.AchievementsScreen
import com.example.checkpoint.ui.screens.ExploreScreen
import com.example.checkpoint.ui.screens.GameScreen
import com.example.checkpoint.ui.screens.GamesGridScreen
import com.example.checkpoint.ui.screens.LibraryScreen
import com.example.checkpoint.ui.screens.LoginScreen
import com.example.checkpoint.ui.screens.ProfileScreen
import com.example.checkpoint.ui.screens.SettingsScreen
import com.example.checkpoint.ui.screens.SignUpScreen
import com.example.checkpoint.ui.theme.CheckpointTheme
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel
import com.example.checkpoint.ui.viewmodel.GameScreenViewModel
import com.example.checkpoint.ui.viewmodel.LibraryViewModel
import com.example.checkpoint.ui.viewmodel.ProfileViewModel
import com.example.checkpoint.ui.viewmodel.SettingsViewModel
import com.example.checkpoint.ui.viewmodel.UiThemeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

	private val sessionManager: SessionManager by inject()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			val settingsViewModel = koinViewModel<SettingsViewModel>()
			val themeState: UiThemeState by settingsViewModel.state.collectAsStateWithLifecycle()

			CheckpointTheme(
				darkTheme = when (themeState.theme) {
					UiTheme.Light -> false
					UiTheme.Dark -> true
					UiTheme.System -> isSystemInDarkTheme()
				}, dynamicColor = themeState.dynamicColor
			) {
				val sessionState by sessionManager.sessionState.collectAsState()

				if (sessionState is SessionState.Loading) {
					Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
						CircularProgressIndicator()
					}
				} else {
					val startDestination = NavigationRoute.ExploreScreen
					val navController = rememberNavController()
					NavGraph(
						navController = navController,
						sessionManager = sessionManager,
						startDestination = startDestination
					)
				}
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
	data class GamesGridScreen(val title: String) : NavigationRoute

	@Serializable
	data class GameScreen(val igdbId: Int) : NavigationRoute

	@Serializable
	data object UserScreen : NavigationRoute

	@Serializable
	data object LibraryScreen : NavigationRoute

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
	sessionManager: SessionManager,
	startDestination: NavigationRoute = NavigationRoute.ExploreScreen,
) {
	// ProfileViewModel is shared between ProfileScreen
	// and SettingsScreen without being recreated on navigation
	val profileViewModel: ProfileViewModel = koinViewModel()

	// AchievementsViewModel is shared between
	// ProfileScreen and AchievementsScreen
	val achievementsViewModel: AchievementsViewModel = koinViewModel()

	NavHost(
		navController = navController, startDestination = startDestination
	) {
		composable<NavigationRoute.ExploreScreen> {
			ExploreScreen(navController)
		}

		composable<NavigationRoute.LibraryScreen> {
			val libraryViewModel = koinViewModel<LibraryViewModel>()
			LibraryScreen(
				navController = navController, vm = libraryViewModel
			)
		}

		composable<NavigationRoute.ProfileScreen> {
			val sessionState by sessionManager.sessionState.collectAsState()

			// Responsive redirection: if the user is not logged in, they go to Login
			LaunchedEffect(sessionState) {
				if (sessionState is SessionState.LoggedOut) {
					navController.navigate(NavigationRoute.LoginScreen) {
						popUpTo(NavigationRoute.ProfileScreen) { inclusive = true }
					}
				}
			}

			if (sessionState is SessionState.LoggedIn) {
				ProfileScreen(
					navController = navController, profileViewModel = profileViewModel
				)
			} else {
				Box(modifier = Modifier.fillMaxSize())
			}
		}
		composable<NavigationRoute.AchievementsScreen> {
			AchievementsScreen(
				navController = navController, achievementsViewModel = achievementsViewModel
			)
		}

		composable<NavigationRoute.SettingsScreen> {
			val settingsViewModel = koinViewModel<SettingsViewModel>()
			val themeState: UiThemeState by settingsViewModel.state.collectAsStateWithLifecycle()
			// AuthRepository is injected directly here for the logout action,
			val authRepository: AuthRepository = org.koin.compose.koinInject()

			SettingsScreen(
				navController = navController,
				themeState = themeState,
				themeActions = settingsViewModel.actions,
				profileViewModel = profileViewModel,
				onLogout = {
					// navigate to LoginScreen on completion
					CoroutineScope(Dispatchers.IO).launch {
						authRepository.logout()
					}
					navController.navigate(NavigationRoute.LoginScreen) {
						popUpTo(0) { inclusive = true }
					}
				})
		}

		composable<NavigationRoute.GameScreen> { backStackEntry ->
			val route: NavigationRoute.GameScreen = backStackEntry.toRoute()
			val vm: GameScreenViewModel = koinViewModel { parametersOf(route.igdbId, 1) }
			GameScreen(
				navController = navController, viewModel = vm
			)
		}

		composable<NavigationRoute.GamesGridScreen> { backStackEntry ->
			val route: NavigationRoute.GamesGridScreen = backStackEntry.toRoute()
			val games =
				navController.previousBackStackEntry?.savedStateHandle?.get<List<Game>>("grid_games")
					?: emptyList()
			GamesGridScreen(
				navController = navController, title = route.title, games = games
			)
		}

		composable<NavigationRoute.LoginScreen> {
			LoginScreen(navController)
		}

		composable<NavigationRoute.SignUpScreen> {
			SignUpScreen(navController)
		}
	}
}