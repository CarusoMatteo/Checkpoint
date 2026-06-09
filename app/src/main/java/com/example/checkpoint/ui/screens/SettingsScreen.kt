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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Button
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

@Composable
fun SettingsScreen(
	navController: NavHostController,
	themeState: UiThemeState,
	themeActions: UiThemeActions,
	profileViewModel: ProfileViewModel,
	onLogout: () -> Unit
) {
	val profileState by profileViewModel.state.collectAsState()
	val currentUser = profileState.user

	AppShell(
		navController = navController,
		title = "User settings",
		selectedNavigationItem = NavigationItem.Profile,
		appBarActions = {
			if (currentUser != null) {
				IconButton(onClick = onLogout) {
					Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout")
				}
			}
		}) { innerPadding ->
		Column(
			Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.verticalScroll(rememberScrollState())
		) {
			// Profile visibility
			if (currentUser != null) {
				ProfileVisibilitySwitch(
					isPublic = currentUser.publicProfile,
					onToggle = { profileViewModel.setProfilePublic(it) },
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

			// password update
			if (currentUser != null) {
				HorizontalDivider(Modifier.padding(horizontal = 16.dp))
				PasswordUpdateForm(
					onUpdatePassword = { current, newPass ->
						// TODO: aggiungere updatePassword a ProfileViewModel
						//       con verifica dell'hash corrente prima di salvare
					},
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
	isPublic: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
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
				.clickable { onToggle(!isPublic) },
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
				checked = isPublic,
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
	onUpdatePassword: (current: String, new: String) -> Unit, modifier: Modifier = Modifier
) {
	val currentPassword = rememberTextFieldState()
	val newPassword = rememberTextFieldState()
	val repeatNewPassword = rememberTextFieldState()
	var errorMessage by remember { mutableStateOf<String?>(null) }

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
						.padding(
							bottom = if (errorMessage != null) 8.dp else 16.dp
						),
				)

				if (errorMessage != null) {
					Text(
						text = errorMessage!!,
						color = MaterialTheme.colorScheme.error,
						style = MaterialTheme.typography.bodySmall,
						modifier = Modifier.padding(bottom = 12.dp)
					)
				}

				Button(
					onClick = {
						val current = currentPassword.text.toString()
						val new = newPassword.text.toString()
						val repeat = repeatNewPassword.text.toString()

						errorMessage = when {
							current.isBlank() || new.isBlank() || repeat.isBlank() -> "Fill in all fields"

							new != repeat -> "New passwords don't match"

							new.length < 8 -> "Password must be at least 8 characters"

							else -> null
						}

						if (errorMessage == null) {
							onUpdatePassword(current, new)
						}
					}, modifier = Modifier.fillMaxWidth()
				) {
					Text("Update Password")
				}
			}
		}
	}
}