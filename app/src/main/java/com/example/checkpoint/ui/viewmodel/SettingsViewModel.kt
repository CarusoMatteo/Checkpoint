package com.example.checkpoint.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.data.repositories.SettingsRepository
import com.example.checkpoint.data.repositories.UiTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiThemeState(
	val theme: UiTheme,
	val dynamicColor: Boolean
)

data class UiThemeActions(
	val setTheme: (UiTheme) -> Unit,
	val setDynamicColor: (Boolean) -> Unit
)

class SettingsViewModel(repository: SettingsRepository) : ViewModel() {
	val state = combine(
		repository.theme,
		repository.dynamicColor
	) { theme, dynamicColor -> UiThemeState(theme, dynamicColor) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(),
			initialValue = UiThemeState(UiTheme.System, false)
		)

	val actions = UiThemeActions(
		setTheme = { theme ->
			viewModelScope.launch { repository.setTheme(theme) }
		},
		setDynamicColor = { enabled ->
			viewModelScope.launch { repository.setDynamicColor(enabled) }
		}
	)
}