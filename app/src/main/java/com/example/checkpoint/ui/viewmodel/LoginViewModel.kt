package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
	val isLoading: Boolean = false, val error: String? = null, val isSuccess: Boolean = false
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
	private val _uiState = MutableStateFlow(LoginUiState())
	val uiState = _uiState.asStateFlow()

	fun login(usernameOrEmail: String, password: String) {
		if (usernameOrEmail.isBlank() || password.isBlank()) {
			_uiState.update { it.copy(error = "Fill in all required fields") }
			return
		}

		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, error = null) }
			authRepository.login(usernameOrEmail, password)
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