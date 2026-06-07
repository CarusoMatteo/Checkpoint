package com.example.checkpoint.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

enum class UiTheme {
	Light, Dark, System
}

class SettingsRepository(
	private val dataStore: DataStore<Preferences>
) {
	companion object {
		// Define the keys that will be used to access the values
		private val THEME_KEY = stringPreferencesKey("theme")
		private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamicColor")
	}

	val theme = dataStore.data.map { preferences ->
		try {
			UiTheme.valueOf(preferences[THEME_KEY] ?: UiTheme.System.name)
		} catch (_: Exception) {
			// You can catch IllegalArgumentException instead of Exception if you want to be more specific
			UiTheme.System
		}
	}

	val dynamicColor = dataStore.data.map { preferences ->
		preferences[DYNAMIC_COLOR_KEY] ?: false
	}

	// Asynchronously save the theme and dynamic color settings to the DataStore
	suspend fun setTheme(theme: UiTheme) = dataStore.edit { preferences ->
		preferences[THEME_KEY] = theme.name
	}

	suspend fun setDynamicColor(enabled: Boolean) = dataStore.edit { preferences ->
		preferences[DYNAMIC_COLOR_KEY] = enabled
	}
}