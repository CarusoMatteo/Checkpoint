package com.example.checkpoint

import android.app.Application
import android.util.Log
import com.example.checkpoint.data.database.AppDatabase
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

private const val TAG = "Checkpoint"

class CheckpointApplication : Application(), KoinComponent {

	/**
	 * Scope tied to the application lifecycle
	 * SupervisorJob makes sure that a failure
	 * in one child you do not erase the others.
	 */
	private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidLogger(Level.ERROR)
			androidContext(this@CheckpointApplication)
			modules(appModule)
		}

		// Force open the DB immediately after Koin is ready
		val db: AppDatabase by inject()
		applicationScope.launch {
			db.openHelper.writableDatabase
		}

		// This MUST happen BEFORE the MainActivity checks sessionState
		val sessionManager: SessionManager by inject()
		applicationScope.launch {
			try {
				Log.d(TAG, "Restoring session from DataStore...")
				sessionManager.restoreSession()
				Log.d(TAG, "Session restore collector ended")
			} catch (e: Exception) {
				Log.e(TAG, "Error restoring session: ${e.message}", e)
			}
		}
	}
}