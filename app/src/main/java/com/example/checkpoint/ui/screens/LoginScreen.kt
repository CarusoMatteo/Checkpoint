package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.NotLoggedInShell
import com.example.checkpoint.ui.viewmodel.LoginUiState
import com.example.checkpoint.ui.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
	navController: NavHostController,
	modifier: Modifier = Modifier,
	viewModel: LoginViewModel = koinViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()

	LaunchedEffect(uiState.isSuccess) {
		if (uiState.isSuccess) {
			navController.navigate(NavigationRoute.ProfileScreen) {
				popUpTo(NavigationRoute.LoginScreen) { inclusive = true }
			}
		}
	}

	AppShell(
		navController = navController,
		title = "Welcome back!",
		selectedNavigationItem = NavigationItem.Profile,
		appBarActions = {
			IconButton(onClick = { /* TODO: Settings */ }) {
				Icon(Icons.Rounded.Settings, contentDescription = "Settings")
			}
		}) { innerPadding ->
		NotLoggedInShell(
			modifier = modifier.padding(innerPadding)
		) { showProgressIndicator ->
			LoginCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				showProgressIndicator = showProgressIndicator,
				navController = navController,
				uiState = uiState,
				onLoginClick = { usernameOrEmail, password ->
					viewModel.login(usernameOrEmail, password)
				})
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginCard(
	modifier: Modifier = Modifier,
	showProgressIndicator: MutableState<Boolean>,
	navController: NavHostController,
	uiState: LoginUiState,
	onLoginClick: (String, String) -> Unit
) {
	val username = rememberTextFieldState("")
	val password = rememberTextFieldState("")
	var showPassword by remember { mutableStateOf(false) }
	var showProgress by showProgressIndicator

	// Reset upload to false if viewModel finishes the operation (e.g. credential error)
	LaunchedEffect(uiState.isLoading) {
		if (!uiState.isLoading) {
			showProgress = false
		}
	}

	OutlinedCard(
		modifier = modifier
	) {
		Text(
			"Welcome back!",
			modifier = Modifier.padding(16.dp),
			style = MaterialTheme.typography.titleLarge
		)

		if (uiState.error != null) {
			Text(
				text = uiState.error,
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			)
		}

		OutlinedTextField(
			state = username,
			label = { Text("Username or Email") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			trailingIcon = {
				if (username.text.isNotEmpty()) {
					IconButton(onClick = { username.clearText() }) {
						Icon(Icons.Rounded.Clear, contentDescription = "Clear username")
					}
				}
			})
		OutlinedSecureTextField(
			state = password,
			label = { Text("Password") },
			textObfuscationMode = if (showPassword) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			trailingIcon = {
				Row {
					if (password.text.isNotEmpty()) {
						IconButton(onClick = { password.clearText() }) {
							Icon(Icons.Rounded.Clear, contentDescription = "Clear password")
						}
					}
					IconButton(
						onClick = { showPassword = !showPassword }) {
						Icon(
							imageVector = if (showPassword) Icons.Rounded.Visibility
							else Icons.Rounded.VisibilityOff, contentDescription = "Clear password"
						)
					}
				}
			})
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text("Don't have an account yet?", modifier = Modifier.weight(1f))
			TextButton(
				onClick = { navController.navigate(NavigationRoute.SignUpScreen) }) {
				Text("Sign up")
			}
		}
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			horizontalArrangement = Arrangement.End
		) {
			Button(onClick = {
				if (!showProgress) {
					showProgress = true
					onLoginClick(username.text.toString(), password.text.toString())
				}
			}) {
				Text("Login")
			}
		}
	}
}

@Preview
@Composable
private fun LoginCardPreview() {
	LoginCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		showProgressIndicator = remember { mutableStateOf(false) },
		navController = rememberNavController(),
		uiState = LoginUiState(),
		onLoginClick = { _, _ -> })
}