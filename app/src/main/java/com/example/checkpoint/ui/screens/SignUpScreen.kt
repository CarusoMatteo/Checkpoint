package com.example.checkpoint.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import com.example.checkpoint.ui.composable.DatePickerField
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.NotLoggedInShell
import com.example.checkpoint.ui.composable.ProfileMonogram
import com.example.checkpoint.ui.composable.ProfileMonogramFontSize
import com.example.checkpoint.ui.viewmodel.SignUpUiState
import com.example.checkpoint.ui.viewmodel.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
	navController: NavHostController,
	modifier: Modifier = Modifier,
	viewModel: SignUpViewModel = koinViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()

	LaunchedEffect(uiState.isSuccess) {
		if (uiState.isSuccess) {
			navController.navigate(NavigationRoute.ProfileScreen) {
				popUpTo(NavigationRoute.SignUpScreen) { inclusive = true }
			}
		}
	}

	AppShell(
		navController,
		title = "Welcome back!",
		selectedNavigationItem = NavigationItem.Profile,
		appBarActions = {
			IconButton(onClick = { /* TODO: Settings */ }) {
				Icon(Icons.Rounded.Settings, contentDescription = "Settings")
			}
		}
	) { innerPadding ->
		NotLoggedInShell(
			modifier = modifier.padding(innerPadding)
		) { showProgressIndicator ->
			SignUpCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				showProgressIndicator = showProgressIndicator,
				navController = navController,
				uiState = uiState,
				onSignUpClick = { username, email, password ->
					viewModel.signUp(username, email, password, "")
				}
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpCard(
	modifier: Modifier = Modifier,
	showProgressIndicator: MutableState<Boolean>,
	navController: NavHostController,
	uiState: SignUpUiState,
	onSignUpClick: (String, String, String) -> Unit
) {
	val email = rememberTextFieldState("")
	val username = rememberTextFieldState("")
	val password = rememberTextFieldState("")
	val repeatPassword = rememberTextFieldState("")
	var showPassword by remember { mutableStateOf(false) }
	var showRepeatPassword by remember { mutableStateOf(false) }
	var showProgress by showProgressIndicator

	// Revert upload to false on error
	LaunchedEffect(uiState.isLoading) {
		if (!uiState.isLoading) {
			showProgress = false
		}
	}

	OutlinedCard(
		modifier = modifier
	) {
		Text(
			"Let's get to know you!",
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
			state = email,
			label = { Text("Email") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			trailingIcon = {
				if (email.text.isNotEmpty()) {
					IconButton(onClick = { email.clearText() }) {
						Icon(Icons.Rounded.Clear, contentDescription = "Clear email")
					}
				}
			}
		)
		OutlinedTextField(
			state = username,
			label = { Text("Username") },
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
			}
		)
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
					IconButton(onClick = { showPassword = !showPassword }) {
						Icon(
							imageVector = if (showPassword) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
							contentDescription = "Toggle password visibility"
						)
					}
				}
			}
		)
		OutlinedSecureTextField(
			state = repeatPassword,
			label = { Text("Repeat password") },
			textObfuscationMode = if (showRepeatPassword) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			trailingIcon = {
				Row {
					if (repeatPassword.text.isNotEmpty()) {
						IconButton(onClick = { repeatPassword.clearText() }) {
							Icon(Icons.Rounded.Clear, contentDescription = "Clear repeat password")
						}
					}
					IconButton(onClick = { showRepeatPassword = !showRepeatPassword }) {
						Icon(
							imageVector = if (showRepeatPassword) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
							contentDescription = "Toggle repeat password visibility"
						)
					}
				}
			}
		)
		DatePickerField(
			label = "Date of birth",
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			onDateSelected = { /* TODO: Handle date selection */ }
		)

		PictureSelector(
			username = username,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp)
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text("Already have an account?", modifier = Modifier.weight(1f))
			TextButton(onClick = { navController.popBackStack() }) {
				Text("Log in")
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
					onSignUpClick(
						username.text.toString(),
						email.text.toString(),
						password.text.toString()
					)
				}
			}) {
				Text("Register")
			}
		}
	}
}

@Composable
fun PictureSelector(
	modifier: Modifier = Modifier,
	username: TextFieldState,
	image: String? = null
) {
	val height = 100.dp

	Row(
		modifier = modifier
	) {
		if (image == null) {
			ProfileMonogram(
				letter = username.text.nullableFirst() ?: 'A',
				modifier = Modifier.size(height),
				fontSize = ProfileMonogramFontSize.Profile
			)
		} else {
			// TODO: Load uploaded image
		}

		Column(
			modifier = Modifier
				.padding(start = 16.dp)
				.weight(1f)
				.fillMaxWidth()
				.height(height),
			verticalArrangement = Arrangement.SpaceAround,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Button(
				onClick = { /* TODO: Take a photo intent */ },
				modifier = Modifier
					.fillMaxWidth()
					.padding(0.dp),
				shape = RoundedCornerShape(
					topStart = 24.dp,
					topEnd = 24.dp,
					bottomStart = 8.dp,
					bottomEnd = 8.dp
				)
			) {
				Icon(
					imageVector = Icons.Rounded.PhotoCamera,
					contentDescription = null,
					modifier = Modifier.padding(end = 8.dp)
				)
				Text("Take a photo")
			}
			FilledTonalButton(
				onClick = { /* TODO: Upload a picture intent */ },
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(
					topStart = 8.dp,
					topEnd = 8.dp,
					bottomStart = 24.dp,
					bottomEnd = 24.dp
				)
			) {
				Icon(
					imageVector = Icons.Rounded.Upload,
					contentDescription = null,
					modifier = Modifier.padding(end = 8.dp)
				)
				Text("Upload a picture")
			}
		}
	}
}

private fun CharSequence.nullableFirst() = if (this.isNotEmpty()) this.first() else null

@Preview
@Composable
private fun PictureSelectorPreview() {
	PictureSelector(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		username = rememberTextFieldState("John Doe")
	)
}

@Preview
@Composable
private fun SignUpPreview() {
	SignUpCard(
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp),
		showProgressIndicator = remember { mutableStateOf(false) },
		navController = rememberNavController(),
		uiState = SignUpUiState(),
		onSignUpClick = { _, _, _ -> }
	)
}