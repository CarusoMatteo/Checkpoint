package com.example.checkpoint.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.checkpoint.BuildConfig
import com.example.checkpoint.data.database.AppDatabase
import com.example.checkpoint.data.database.DatabaseSeeder
import com.example.checkpoint.data.remote.igdb.IgdbClient
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.repositories.GameLogRepository
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.repositories.ReviewRepository
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel
import com.example.checkpoint.ui.viewmodel.ExploreViewModel
import com.example.checkpoint.ui.viewmodel.GameScreenViewModel
import com.example.checkpoint.ui.viewmodel.ProfileViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

	// ── Ktor

	single {
		HttpClient(Android) {
			install(ContentNegotiation) {
				json(Json {
					ignoreUnknownKeys = true
					isLenient = true
				})
			}
			install(Logging) {
				level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
			}
		}
	}

	// ── IGDB

	single {
		IgdbClient(
			httpClient = get(),
			clientId = BuildConfig.IGDB_CLIENT_ID,
			accessToken = BuildConfig.IGDB_ACCESS_TOKEN
		)
	}

	// ── Room + Seed

	single {
		Room.databaseBuilder(
			androidContext(), AppDatabase::class.java, "checkpoint-db"
		).fallbackToDestructiveMigration()   // durante lo sviluppo; rimuovere in produzione
			.addCallback(object : RoomDatabase.Callback() {
				override fun onCreate(db: SupportSQLiteDatabase) {
					super.onCreate(db)
					// Eseguito solo alla prima creazione del DB
					CoroutineScope(Dispatchers.IO).launch {
						val database = get<AppDatabase>()
						DatabaseSeeder.seed(
							userDao = database.userDao(),
							achievementDao = database.achievementDao(),
							userAchievementDao = database.userAchievementDao(),
							reviewDao = database.reviewDao(),
						)
					}
				}
			}).build()
	}

	// ── DAO

	single { get<AppDatabase>().userDao() }
	single { get<AppDatabase>().gameDao() }
	single { get<AppDatabase>().platformDao() }
	single { get<AppDatabase>().genreDao() }
	single { get<AppDatabase>().gameLogDao() }
	single { get<AppDatabase>().reviewDao() }
	single { get<AppDatabase>().gameListDao() }
	single { get<AppDatabase>().listEntryDao() }
	single { get<AppDatabase>().achievementDao() }
	single { get<AppDatabase>().userAchievementDao() }
	single { get<AppDatabase>().gamePlatformDao() }
	single { get<AppDatabase>().userPreferredGenreDao() }

	// ── Repository

	single {
		GameRepository(
			gameDao = get(),
			platformDao = get(),
			genreDao = get(),
			gamePlatformDao = get(),
			igdbClient = get()
		)
	}
	single { GameLogRepository(gameLogDao = get()) }
	single { ReviewRepository(reviewDao = get()) }
	single { GameListRepository(gameListDao = get(), listEntryDao = get()) }
	single { AchievementRepository(achievementDao = get(), userAchievementDao = get()) }

	// ── ViewModel

	viewModel { AchievementsViewModel() }
	viewModel { ExploreViewModel(gameRepository = get()) }
	viewModel { ProfileViewModel(reviewRepository = get(), achievementRepository = get()) }
	viewModel { (igdbId: Int, userId: Int) ->
		GameScreenViewModel(
			igdbId = igdbId,
			userId = userId,
			gameRepository = get(),
			gameLogRepository = get(),
			reviewRepository = get()
		)
	}

}
