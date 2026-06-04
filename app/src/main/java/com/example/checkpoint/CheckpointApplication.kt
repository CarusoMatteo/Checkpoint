package com.example.checkpoint

import android.app.Application
import android.util.Log
import com.example.checkpoint.data.database.AppDatabase
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

private const val TAG = "Checkpoint"

class CheckpointApplication : Application(), KoinComponent {

	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidLogger(Level.ERROR)
			androidContext(this@CheckpointApplication)
			modules(appModule)
		}

		// Forza l'apertura del DB subito dopo che Koin è pronto
		val db: AppDatabase by inject()
		CoroutineScope(Dispatchers.IO).launch {
			db.openHelper.writableDatabase
		}

		// Questo DEVE avvenire PRIMA che MainActivity controlla sessionState
		// senno  MainActivity vede sempre LoggedOut
		val sessionManager: SessionManager by inject()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				Log.d(TAG, "Restoring session from DataStore...")
				sessionManager.restoreSession()
				Log.d(TAG, "Session restored")
			} catch (e: Exception) {
				Log.e(TAG, "Error restoring session: ${e.message}", e)
			}
		}
	}
}