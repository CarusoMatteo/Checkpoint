package com.example.checkpoint

import android.app.Application
import com.example.checkpoint.data.database.AppDatabase
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

class CheckpointApplication : Application(), KoinComponent {

	override fun onCreate() {
		super.onCreate()

		startKoin {
			androidLogger(Level.ERROR)
			androidContext(this@CheckpointApplication)
			modules(appModule)
		}

		// Forza l'apertura del DB subito dopo che Koin è pronto.
		//
		// Senza questo, tutti i single{} di Koin restano lazy (eagerInstances=0
		// nel debug): AppDatabase non viene mai costruito, il file .db non esiste
		// su disco e App Inspection non vede nulla.
		//
		//10 ore per capirlo!!!!!
		//
		// inject() è disponibile grazie a KoinComponent implementato sopra.
		// openHelper.writableDatabase apre fisicamente il file SQLite, triggera
		// il Callback.onCreate() con il seed e rende il DB visibile ad App Inspection.
		val db: AppDatabase by inject()
		CoroutineScope(Dispatchers.IO).launch {
			db.openHelper.writableDatabase
		}
	}
}