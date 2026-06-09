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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.checkpoint.NavigationRoute
import com.example.checkpoint.ui.composable.AppShell
import com.example.checkpoint.ui.composable.DatePickerField
import com.example.checkpoint.ui.composable.NavigationItem
import com.example.checkpoint.ui.viewmodel.SignUpUiState
import com.example.checkpoint.ui.viewmodel.SignUpViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

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
		navController = navController,
		title = "Sign Up",
		selectedNavigationItem = NavigationItem.Profile
	) { innerPadding ->
		Column(
			modifier = modifier
				.fillMaxSize()
				.verticalScroll(rememberScrollState())
				.padding(innerPadding)
				.padding(16.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			SignUpCard(
				modifier = Modifier.fillMaxWidth(),
				uiState = uiState,
				navController = navController,
				onSignUpClick = { username, email, password, avatarUri ->
					viewModel.signUp(
						context = context,
						username = username,
						email = email,
						password = password,
						bio = "",
						avatarUri = avatarUri
					)
				})
		}
	}
}

@Composable
private fun SignUpCard(
	modifier: Modifier = Modifier,
	uiState: SignUpUiState,
	navController: NavHostController,
	onSignUpClick: (String, String, String, Uri?) -> Unit
) {
	val username = rememberTextFieldState("")
	val email = rememberTextFieldState("")
	val password = rememberTextFieldState("")
	val repeatPassword = rememberTextFieldState("")

	var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
	var isOldEnough by remember { mutableStateOf(false) }
	var dateError by remember { mutableStateOf<String?>(null) }

	OutlinedCard(modifier = modifier) {
		Text(
			"Let's get to know you!",
			modifier = Modifier.padding(16.dp),
			style = MaterialTheme.typography.titleLarge,
			fontWeight = FontWeight.Bold
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

		OutlinedTextField(
			state = username,
			label = { Text("Username") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp)
		)
		OutlinedTextField(
			state = email,
			label = { Text("Email") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp)
		)
		OutlinedTextField(
			state = password,
			label = { Text("Password") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp)
		)
		OutlinedTextField(
			state = repeatPassword,
			label = { Text("Repeat Password") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp)
		)

		DatePickerField(
			label = "Date of birth",
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp),
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
				.padding(horizontal = 16.dp, vertical = 16.dp)
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text("Already have an account?")
			TextButton(onClick = { navController.popBackStack() }) { Text("Log in") }
		}

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.End
		) {
			Button(
				onClick = {
					onSignUpClick(
						username.text.toString(),
						email.text.toString(),
						password.text.toString(),
						selectedImageUri
					)
				},
				enabled = !uiState.isLoading && isOldEnough && password.text.isNotBlank() && password.text.toString() == repeatPassword.text.toString()
			) {
				Text(if (uiState.isLoading) "Registering..." else "Register")
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
				context, "${context.packageName}.fileprovider",//in manifest
				tempFile
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

	Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
		if (selectedImageUri == null) {
			val letter = if (username.text.isNotEmpty()) username.text.first() else 'A'
			Text(
				text = letter.toString().uppercase(),
				style = MaterialTheme.typography.headlineLarge,
				modifier = Modifier
					.size(100.dp)
					.clip(RoundedCornerShape(8.dp))
					.padding(24.dp)
			)
		} else {
			AsyncImage(
				model = selectedImageUri,
				contentDescription = "Selected avatar",
				modifier = Modifier
					.size(100.dp)
					.clip(RoundedCornerShape(8.dp)),
				contentScale = ContentScale.Crop
			)
		}

		Column(
			modifier = Modifier
				.padding(start = 16.dp)
				.weight(1f)
				.height(100.dp),
			verticalArrangement = Arrangement.SpaceBetween
		) {
			Button(
				onClick = {
					val hasPermission = ContextCompat.checkSelfPermission(
						context, Manifest.permission.CAMERA
					) == PackageManager.PERMISSION_GRANTED

					if (hasPermission) {
						// already has permission
						launchCameraAction()
					} else {
						// no permission yet
						requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
					}
				}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(
					topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp
				)
			) {
				Icon(
					Icons.Rounded.PhotoCamera,
					contentDescription = null,
					modifier = Modifier.padding(end = 8.dp)
				)
				Text("Take a photo")
			}

			// Gallery Picker
			FilledTonalButton(
				onClick = {
					pickMediaLauncher.launch(
						PickVisualMediaRequest(PickVisualMedia.ImageOnly)
					)
				}, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(
					topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp
				)
			) {
				Icon(
					Icons.Rounded.Upload,
					contentDescription = null,
					modifier = Modifier.padding(end = 8.dp)
				)
				Text("Upload a picture")
			}
		}
	}
}