package com.example.checkpoint.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class SignUpUiState(
	val isLoading: Boolean = false,
	val error: String? = null,
	val isSuccess: Boolean = false
)

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

	private val _uiState = MutableStateFlow(SignUpUiState())
	val uiState = _uiState.asStateFlow()

	fun signUp(
		context: Context,
		username: String,
		email: String,
		password: String,
		bio: String,
		avatarUri: Uri?
	) {
		if (username.isBlank() || email.isBlank() || password.isBlank()) {
			_uiState.update { it.copy(error = "Fill in all required fields") }
			return
		}

		_uiState.update { it.copy(isLoading = true, error = null) }

		viewModelScope.launch {
			authRepository.signUp(username, email, password, bio)
				.onSuccess { user ->
					if (avatarUri != null) {
						saveAvatarToInternalStorage(context, avatarUri, user.id)?.let { path ->
							authRepository.updateAvatarUrl(user.id, path)
						}
					}
					_uiState.update { it.copy(isLoading = false, isSuccess = true) }
				}
				.onFailure { ex ->
					_uiState.update {
						it.copy(
							isLoading = false,
							error = ex.localizedMessage ?: "Error during registration"
						)
					}
				}
		}
	}

	private fun saveAvatarToInternalStorage(context: Context, uri: Uri, userId: Int): String? {
		return try {
			val avatarDir = File(context.filesDir, "avatars").also { it.mkdirs() }
			val destFile = File(avatarDir, "user_$userId.jpg")
			context.contentResolver.openInputStream(uri)?.use { input ->
				FileOutputStream(destFile).use { output -> input.copyTo(output) }
			}
			destFile.absolutePath
		} catch (_: Exception) {
			null
		}
	}
}