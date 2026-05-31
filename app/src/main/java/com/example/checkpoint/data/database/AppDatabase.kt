package com.example.checkpoint.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.checkpoint.data.database.daos.AchievementDao
import com.example.checkpoint.data.database.daos.GameDao
import com.example.checkpoint.data.database.daos.GameListDao
import com.example.checkpoint.data.database.daos.GameLogDao
import com.example.checkpoint.data.database.daos.GamePlatformDao
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.ListEntryDao
import com.example.checkpoint.data.database.daos.PlatformDao
import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.daos.UserAchievementDao
import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.daos.UserPreferredGenreDao
import com.example.checkpoint.data.database.entities.AchievementCategoryEntity
import com.example.checkpoint.data.database.entities.AchievementEntity
import com.example.checkpoint.data.database.entities.GameEntity
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.database.entities.GameLogEntity
import com.example.checkpoint.data.database.entities.GamePlatformEntity
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.ListEntryEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserAchievementEntity
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.database.entities.UserPreferredGenreEntity

@Database(
	entities = [
		UserEntity::class,
		UserPreferredGenreEntity::class,
		GameEntity::class,
		PlatformEntity::class,
		GenreEntity::class,
		GamePlatformEntity::class,
		GameLogEntity::class,
		ReviewEntity::class,
		GameListEntity::class,
		ListEntryEntity::class,
		AchievementCategoryEntity::class,
		AchievementEntity::class,
		UserAchievementEntity::class,
	], version = 2, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao
	abstract fun gameDao(): GameDao
	abstract fun platformDao(): PlatformDao
	abstract fun genreDao(): GenreDao
	abstract fun gameLogDao(): GameLogDao
	abstract fun reviewDao(): ReviewDao
	abstract fun gameListDao(): GameListDao
	abstract fun listEntryDao(): ListEntryDao
	abstract fun achievementDao(): AchievementDao
	abstract fun userAchievementDao(): UserAchievementDao
	abstract fun gamePlatformDao(): GamePlatformDao
	abstract fun userPreferredGenreDao(): UserPreferredGenreDao
}
