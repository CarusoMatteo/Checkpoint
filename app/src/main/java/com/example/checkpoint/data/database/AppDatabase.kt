package com.example.checkpoint.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.checkpoint.data.database.daos.GameLogDao
import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.entities.GameEntity
import com.example.checkpoint.data.database.entities.GameLogEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import com.example.checkpoint.data.database.entities.UserEntity


@Database(
	entities = [
		UserEntity::class,
		GameEntity::class,
		PlatformEntity::class,
		GameLogEntity::class,
	], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun userDao(): UserDao

	abstract fun gameLogDao(): GameLogDao

}
