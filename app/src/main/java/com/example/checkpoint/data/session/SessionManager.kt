package com.example.checkpoint.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Represents the current user authentication state within the application.
 */
sealed interface SessionState {
	object Loading : SessionState
	object LoggedOut : SessionState
	data class LoggedIn(val userId: Int, val username: String) : SessionState
}

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore("session")
private val USER_ID_KEY = intPreferencesKey("user_id")
private val USERNAME_KEY = stringPreferencesKey("username")

/**
 * Manages user authentication state and handles persistent session storage using Jetpack DataStore.
 */
class SessionManager(private val context: Context) {

	private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
	val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

	/**
	 * Restores the user session from disk.
	 * Should be invoked during application initialization before rendering the UI.
	 */
	suspend fun restoreSession() {
		context.sessionDataStore.data.map { prefs ->
			val userId = prefs[USER_ID_KEY]
			val username = prefs[USERNAME_KEY]

			when {
				userId != null && username != null -> SessionState.LoggedIn(userId, username)
				else -> SessionState.LoggedOut
			}
		}.collect { _sessionState.value = it }
	}

	/**
	 * Persists the user credentials to storage and transitions the session state to [SessionState.LoggedIn].
	 */
	suspend fun login(userId: Int, username: String) {
		context.sessionDataStore.edit { prefs ->
			prefs[USER_ID_KEY] = userId
			prefs[USERNAME_KEY] = username
		}
		_sessionState.value = SessionState.LoggedIn(userId, username)
	}

	/**
	 * Clears all session data from storage and transitions the session state to [SessionState.LoggedOut].
	 */
	suspend fun logout() {
		context.sessionDataStore.edit { prefs ->
			prefs.remove(USER_ID_KEY)
			prefs.remove(USERNAME_KEY)
		}
		_sessionState.value = SessionState.LoggedOut
	}
}