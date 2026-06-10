package com.example.checkpoint.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.DatePickerField
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.composable.NotLoggedInShell
import com.example.checkpoint.ui.composable.PasswordOutlinedTextField
import com.example.checkpoint.ui.composable.ProfileMonogram
import com.example.checkpoint.ui.composable.ProfileMonogramFontSize
import com.example.checkpoint.ui.viewmodel.SignUpUiState
import com.example.checkpoint.ui.viewmodel.SignUpViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
	navController: NavHostController,
	modifier: Modifier = Modifier,
	viewModel: SignUpViewModel = koinViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()
	val context = LocalContext.current

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
			IconButton(onClick = { navController.navigate(NavigationRoute.SettingsScreen) }) {
				Icon(Icons.Rounded.Settings, contentDescription = "Settings")
			}
		}) { innerPadding ->
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
				onSignUpClick = { username, email, password, avatarUri ->
					viewModel.signUp(
						context, username, email, password, "", avatarUri
					)
				})
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
	onSignUpClick: (String, String, String, Uri?) -> Unit
) {
	val email = rememberTextFieldState("")
	val username = rememberTextFieldState("")
	val password = rememberTextFieldState("")
	val repeatPassword = rememberTextFieldState("")
	var showProgress by showProgressIndicator

	var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
	var isOldEnough by remember { mutableStateOf(false) }
	var dateError by remember { mutableStateOf<String?>(null) }

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

		if (dateError != null) {
			Text(
				text = dateError!!,
				color = MaterialTheme.colorScheme.error,
				style = MaterialTheme.typography.bodyMedium,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			)
		}
		if (password.text.isNotEmpty() && password.text.length < 8) {
			Text(
				text = "The password must be at least 8 characters long.",
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
			})
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
			})
		PasswordOutlinedTextField(
			state = password,
			label = { Text("Password") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)
		PasswordOutlinedTextField(
			state = repeatPassword,
			label = { Text("Repeat Password") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
		)
		DatePickerField(
			label = "Date of birth",
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp)
				.padding(bottom = 16.dp),
			onDateSelected = { timestamp ->
				if (timestamp != null) {
					val birthDate =
						Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
					val age = Period.between(birthDate, LocalDate.now()).years
					if (age < 18) {
						isOldEnough = false
						dateError = "You must be at least 18 years old to register."
					} else {
						isOldEnough = true
						dateError = null
					}
				} else {
					isOldEnough = false
					dateError = null
				}
			})

		PictureSelector(
			username = username,
			selectedImageUri = selectedImageUri,
			onImageSelected = { uri -> selectedImageUri = uri },
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
			Button(
				onClick = {
					if (!showProgress) {
						showProgress = true
						onSignUpClick(
							username.text.toString(),
							email.text.toString(),
							password.text.toString(),
							selectedImageUri
						)
					}
				},
				enabled = !uiState.isLoading && isOldEnough && password.text.length >= 8 && password.text.toString() == repeatPassword.text.toString()
			) {
				Text("Register")
			}
		}
	}
}

@Composable
fun PictureSelector(
	modifier: Modifier = Modifier,
	username: TextFieldState,
	selectedImageUri: Uri?,
	onImageSelected: (Uri?) -> Unit
) {
	val context = LocalContext.current
	var cameraUri by remember { mutableStateOf<Uri?>(null) }
	val height = 100.dp

	// Gallery Picker
	val pickMediaLauncher = rememberLauncherForActivityResult(
		contract = PickVisualMedia()
	) { uri: Uri? ->
		if (uri != null) onImageSelected(uri)
	}

	// Launcher to take the photo
	val takePictureLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture()
	) { success ->
		if (success) cameraUri?.let { onImageSelected(it) }
	}

	val launchCameraAction = {
		try {
			val tempFile = File(context.cacheDir, "camera_avatar_temp.jpg")
			if (tempFile.exists()) tempFile.delete()
			tempFile.createNewFile()

			val uri = FileProvider.getUriForFile(
				context, "${context.packageName}.fileprovider", tempFile
			)
			cameraUri = uri
			takePictureLauncher.launch(uri)
		} catch (e: Exception) {
			Log.e("CAMERA_ERR", "Error launching camera", e)
			Toast.makeText(context, "Camera error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
		}
	}

	// request Permission
	val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission()
	) { isGranted ->
		if (isGranted) {
			launchCameraAction()
		}
	}

	Row(
		modifier = modifier
	) {
		if (selectedImageUri == null) {
			ProfileMonogram(
				letter = username.text.nullableFirst() ?: 'A',
				modifier = Modifier.size(height),
				fontSize = ProfileMonogramFontSize.Profile
			)
		} else {
			AsyncImage(
				model = selectedImageUri,
				contentDescription = "Selected avatar",
				modifier = Modifier
					.size(height)
					.clip(RoundedCornerShape(8.dp)),
				contentScale = ContentScale.Crop
			)
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
				onClick = {
					val hasPermission = ContextCompat.checkSelfPermission(
						context, Manifest.permission.CAMERA
					) == PackageManager.PERMISSION_GRANTED

					if (hasPermission) {
						launchCameraAction()
					} else {
						requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
					}
				}, modifier = Modifier
					.fillMaxWidth()
					.padding(0.dp), shape = RoundedCornerShape(
					topStart = 24.dp, topEnd = 24.dp, bottomStart = 8.dp, bottomEnd = 8.dp
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
				onClick = {
					pickMediaLauncher.launch(
						PickVisualMediaRequest(PickVisualMedia.ImageOnly)
					)
				}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(
					topStart = 8.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 24.dp
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
		username = rememberTextFieldState("John Doe"),
		selectedImageUri = null,
		onImageSelected = {})
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
		onSignUpClick = { _, _, _, _ -> })
}