package com.example.checkpoint.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.checkpoint.data.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

	@Query("SELECT * FROM users WHERE id = :id LIMIT 1")
	fun getUserById(id: Int): Flow<UserEntity?>

	@Query("SELECT * FROM users WHERE username = :username LIMIT 1")
	suspend fun getUserByUsername(username: String): UserEntity?

	@Query("SELECT * FROM users WHERE email = :email LIMIT 1")
	suspend fun getUserByEmail(email: String): UserEntity?

	@Upsert
	suspend fun upsert(user: UserEntity): Long

	@Delete
	suspend fun delete(user: UserEntity)
}
