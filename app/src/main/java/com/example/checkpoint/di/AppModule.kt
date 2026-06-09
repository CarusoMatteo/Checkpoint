package com.example.checkpoint.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.checkpoint.BuildConfig
import com.example.checkpoint.data.achievements.AchievementEvaluator
import com.example.checkpoint.data.database.AppDatabase
import com.example.checkpoint.data.database.DatabaseSeeder
import com.example.checkpoint.data.remote.igdb.IgdbClient
import com.example.checkpoint.data.repositories.AchievementRepository
import com.example.checkpoint.data.repositories.AuthRepository
import com.example.checkpoint.data.repositories.GameListRepository
import com.example.checkpoint.data.repositories.GameLogRepository
import com.example.checkpoint.data.repositories.GameRepository
import com.example.checkpoint.data.repositories.ReviewRepository
import com.example.checkpoint.data.repositories.SettingsRepository
import com.example.checkpoint.data.repositories.UserRepository
import com.example.checkpoint.data.session.SessionManager
import com.example.checkpoint.ui.viewmodel.AchievementsViewModel
import com.example.checkpoint.ui.viewmodel.ExploreViewModel
import com.example.checkpoint.ui.viewmodel.GameScreenViewModel
import com.example.checkpoint.ui.viewmodel.LibraryViewModel
import com.example.checkpoint.ui.viewmodel.LoginViewModel
import com.example.checkpoint.ui.viewmodel.ProfileViewModel
import com.example.checkpoint.ui.viewmodel.SettingsViewModel
import com.example.checkpoint.ui.viewmodel.SignUpViewModel
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

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {

	// ── Session

	single { SessionManager(context = androidContext()) }

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
			androidContext(), AppDatabase::class.java, "checkpoint.db"
		).addCallback(object : RoomDatabase.Callback() {
			override fun onCreate(db: SupportSQLiteDatabase) {
				super.onCreate(db)
				CoroutineScope(Dispatchers.IO).launch {
					val database = get<AppDatabase>()
					DatabaseSeeder.seed(db = database)
				}
			}
		}).build()
	}

	// ── DataStore

	single { get<Context>().dataStore }

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
	single { get<AppDatabase>().achievementMetricsDao() }

	// ── Repository

	single { AuthRepository(userDao = get(), sessionManager = get()) }
	single {
		GameRepository(
			gameDao = get(),
			platformDao = get(),
			genreDao = get(),
			gamePlatformDao = get(),
			igdbClient = get()
		)
	}
	single { UserRepository(get()) }
	single { GameLogRepository(gameLogDao = get()) }
	single { ReviewRepository(reviewDao = get()) }
	single { GameListRepository(gameListDao = get(), listEntryDao = get()) }
	single { AchievementRepository(achievementDao = get(), userAchievementDao = get()) }
	single { SettingsRepository(get()) }

	// ── Achievement evaluator

	single { AchievementEvaluator(metricsDao = get(), achievementRepository = get()) }

	// ── ViewModel

	viewModel { ExploreViewModel(gameRepository = get()) }
	viewModel { LoginViewModel(authRepository = get()) }
	viewModel { SignUpViewModel(authRepository = get()) }
	viewModel {
		AchievementsViewModel(
			sessionManager = get(), achievementRepository = get(), achievementEvaluator = get()
		)
	}
	viewModel {
		ProfileViewModel(
			sessionManager = get(),
			userRepository = get(),
			gameRepository = get(),
			gameListRepository = get(),
			achievementRepository = get(),
			genreDao = get(),
			userPreferredGenreDao = get(),
			reviewDao = get()
		)
	}
	viewModel {
		LibraryViewModel(
			sessionManager = get(), gameListRepository = get(), igdbClient = get()
		)
	}
	viewModel { (igdbId: Int, userId: Int) ->
		GameScreenViewModel(
			igdbId = igdbId,
			userId = userId,
			gameRepository = get(),
			gameLogRepository = get(),
			reviewRepository = get(),
			gameListRepository = get(),
			userRepository = get()
		)
	}
	viewModel { SettingsViewModel(get()) }
}