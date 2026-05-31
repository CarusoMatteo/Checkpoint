package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User logged into the app.
 * [publicProfile] determines whether the profile is visible to other users.
 */
@Entity(tableName = "users")
data class UserEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,

	@ColumnInfo(name = "username") val username: String,

	@ColumnInfo(name = "email") val email: String,

	@ColumnInfo(name = "password_hash") val passwordHash: String,

	@ColumnInfo(name = "avatar_url") val avatarUrl: String? = null,

	@ColumnInfo(name = "bio") val bio: String? = null,

	@ColumnInfo(name = "created_at") val createdAt: String? = null,

	@ColumnInfo(name = "updated_at") val updatedAt: String? = null,

	@ColumnInfo(name = "public_profile") val publicProfile: Boolean = true
)
