package com.example.checkpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import com.example.checkpoint.NavigationRoute.AccountScreen
import com.example.checkpoint.NavigationRoute.AchievementsScreen
import com.example.checkpoint.NavigationRoute.ExploreScreen
import com.example.checkpoint.NavigationRoute.GameScreen
import com.example.checkpoint.NavigationRoute.GamesGridScreen
import com.example.checkpoint.NavigationRoute.LibraryScreen
import com.example.checkpoint.NavigationRoute.LoginScreen
import com.example.checkpoint.NavigationRoute.ProfileScreen
import com.example.checkpoint.NavigationRoute.RegisterScreen
import com.example.checkpoint.NavigationRoute.SearchScreen
import com.example.checkpoint.NavigationRoute.UserScreen
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.theme.CheckpointTheme
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			CheckpointTheme {
				val navController = rememberNavController()
				AppShell(navController)
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

fun NavigationRoute.title(): String {
	return when (this) {
		ExploreScreen -> "Explore"
		GamesGridScreen -> "Games Grid List"
		SearchScreen -> "Search"
		GameScreen -> "Game"
		UserScreen -> "User"
		LibraryScreen -> "Library"
		AccountScreen -> "Account"
		LoginScreen -> "Login"
		RegisterScreen -> "Register"
		ProfileScreen -> "Profile"
		AchievementsScreen -> "Achievements"
	}
}