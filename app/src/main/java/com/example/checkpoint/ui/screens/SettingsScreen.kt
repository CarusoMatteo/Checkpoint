package com.example.checkpoint.ui.screens

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.checkpoint.data.repositories.UiTheme
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.PasswordOutlinedTextField
import com.example.checkpoint.ui.composable.RadioListItem
import com.example.checkpoint.ui.viewmodel.ProfileViewModel
import com.example.checkpoint.ui.viewmodel.UiThemeActions
import com.example.checkpoint.ui.viewmodel.UiThemeState
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
	navController: NavHostController,
	themeState: UiThemeState,
	themeActions: UiThemeActions,
	profileViewModel: ProfileViewModel,
	onLogout: () -> Unit
) {
	val uiState by profileViewModel.state.collectAsState()
	val isLoggedIn = uiState.user != null

	AppShell(
		navController = navController,
		title = "User settings",
		selectedNavigationItem = NavigationItem.Profile,
		appBarActions = {
			IconButton(onClick = onLogout) {
				Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout")
			}
		}) { innerPadding ->
		Column(
			Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
		) {
			if (isLoggedIn) {
				ProfileVisibilitySwitch(
					profileViewModel = profileViewModel,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
						.padding(vertical = 8.dp)
				)
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
			}

			UiThemeSelector(
				themeState = themeState,
				themeActions = themeActions,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
					.padding(vertical = 8.dp)
			)

			if (isLoggedIn) {
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
				PasswordUpdateForm(
					profileViewModel = profileViewModel,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp)
						.padding(vertical = 8.dp)
				)
			}
		}
	}
}

@Composable
fun ProfileVisibilitySwitch(
	profileViewModel: ProfileViewModel, modifier: Modifier = Modifier
) {
	val uiState by profileViewModel.state.collectAsState()
	val isProfilePublic = uiState.user?.publicProfile ?: true

	Column(modifier = modifier) {
		Text(
			text = "Profile visibility",
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(bottom = 8.dp)
		)
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.height(48.dp)
				.clickable(onClick = {
					profileViewModel.setProfilePublic(!isProfilePublic)
				}),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				"Profile is visible to others", modifier = Modifier
					.weight(1f)
					.basicMarquee()
			)
			Switch(
				modifier = Modifier.padding(start = 8.dp),
				checked = isProfilePublic,
				onCheckedChange = null
			)
		}
	}
}

@Composable
fun UiThemeSelector(
	themeState: UiThemeState, themeActions: UiThemeActions, modifier: Modifier = Modifier
) {
	Column(modifier = modifier) {
		Text(
			text = "UI Theme",
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(vertical = 8.dp)
		)
		UiTheme.entries.forEach { theme ->
			RadioListItem(
				text = theme.name,
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp),
				selected = theme == themeState.theme,
				onClick = { themeActions.setTheme(theme) })
		}

		Text(
			text = "UI Color Scheme",
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(vertical = 8.dp)
		)
		listOf(true, false).forEach { dynamicColorEnabled ->
			RadioListItem(
				text = if (dynamicColorEnabled) "System colors" else "Custom colors",
				selected = dynamicColorEnabled == themeState.dynamicColor,
				onClick = { themeActions.setDynamicColor(dynamicColorEnabled) },
				modifier = Modifier
					.fillMaxWidth()
					.height(48.dp)
			)
		}
	}
}

@Composable
fun PasswordUpdateForm(
	profileViewModel: ProfileViewModel, modifier: Modifier = Modifier
) {
	val currentPassword = rememberTextFieldState()
	val newPassword = rememberTextFieldState()
	val repeatNewPassword = rememberTextFieldState()

	var errorMessage by remember { mutableStateOf<String?>(null) }
	var successMessage by remember { mutableStateOf<String?>(null) }
	var isLoading by remember { mutableStateOf(false) }
	val scope = rememberCoroutineScope()

	Column(modifier = modifier) {
		Text(
			text = "Change Password",
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(vertical = 8.dp)
		)
		ElevatedCard(modifier = Modifier.fillMaxWidth()) {
			Column(Modifier.padding(16.dp)) {
				PasswordOutlinedTextField(
					state = currentPassword,
					label = { Text("Current password") },
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 16.dp),
				)
				PasswordOutlinedTextField(
					state = newPassword,
					label = { Text("New password") },
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 16.dp),
				)
				PasswordOutlinedTextField(
					state = repeatNewPassword,
					label = { Text("Repeat new password") },
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 16.dp),
				)

				if (errorMessage != null) {
					Text(
						text = errorMessage!!,
						color = MaterialTheme.colorScheme.error,
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(bottom = 12.dp)
					)
				}

				if (successMessage != null) {
					Text(
						text = successMessage!!,
						color = MaterialTheme.colorScheme.primary,
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(bottom = 12.dp)
					)
				}

				Button(
					onClick = {
						val currentPwdText = currentPassword.text.toString()
						val newPwdText = newPassword.text.toString()
						val repeatNewPwdText = repeatNewPassword.text.toString()

						errorMessage = when {
							currentPwdText.isBlank() || newPwdText.isBlank() || repeatNewPwdText.isBlank() -> "Fill in all fields"
							newPwdText != repeatNewPwdText -> "New passwords don't match"
							newPwdText.length < 8 -> "The new password must be at least 8 characters long"
							else -> null
						}

						if (errorMessage == null) {
							isLoading = true
							scope.launch {
								val result =
									profileViewModel.updatePassword(currentPwdText, newPwdText)
								isLoading = false
								if (result.isSuccess) {
									successMessage = "Password updated successfully!"
									currentPassword.clearText()
									newPassword.clearText()
									repeatNewPassword.clearText()
								} else {
									errorMessage = result.exceptionOrNull()?.message
										?: "Error during update"
								}
							}
						}
					}, enabled = !isLoading, modifier = Modifier.fillMaxWidth()
				) {
					if (isLoading) {
						CircularProgressIndicator(
							modifier = Modifier.size(20.dp),
							strokeWidth = 2.dp,
							color = MaterialTheme.colorScheme.onPrimary
						)
					} else {
						Text("Update Password")
					}
				}
			}
		}
	}
}