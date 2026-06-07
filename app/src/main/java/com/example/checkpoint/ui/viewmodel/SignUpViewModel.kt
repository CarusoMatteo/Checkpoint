package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
	val isLoading: Boolean = false, val error: String? = null, val isSuccess: Boolean = false
)

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {
	private val _uiState = MutableStateFlow(SignUpUiState())
	val uiState = _uiState.asStateFlow()

	fun signUp(username: String, email: String, password: String, bio: String) {
		if (username.isBlank() || email.isBlank() || password.isBlank()) {
			_uiState.update { it.copy(error = "Fill in all required fields") }
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null) }
			authRepository.signUp(username, email, password, bio)
				.onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
				.onFailure { ex ->
					_uiState.update {
						it.copy(
							isLoading = false, error = ex.localizedMessage
						)
					}
				}
		}
	}
}