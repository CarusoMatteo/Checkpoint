package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Review written by a user for a game.
 * A user can write at most one review per game
 * (UNIQUE constraint on user_id + game_id).
 */
@Entity(
	tableName = "reviews", foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["user_id"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = GameEntity::class,
		parentColumns = ["id"],
		childColumns = ["game_id"],
		onDelete = ForeignKey.CASCADE
	)], indices = [Index(value = ["user_id", "game_id"], unique = true), Index("game_id")]
)
data class ReviewEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,

	@ColumnInfo(name = "user_id") val userId: Int,

	@ColumnInfo(name = "game_id") val gameId: Int,

	/** Rating from 1 to 10 */
	@ColumnInfo(name = "rating") val rating: Int,

	@ColumnInfo(name = "body") val body: String,

	@ColumnInfo(name = "contains_spoilers") val containsSpoilers: Boolean = false,

	@ColumnInfo(name = "created_at") val createdAt: String? = null,

	@ColumnInfo(name = "updated_at") val updatedAt: String? = null
)