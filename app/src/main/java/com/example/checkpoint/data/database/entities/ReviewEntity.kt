package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.checkpoint.data.repositories.CompletionType

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
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	@ColumnInfo(name = "user_id") val userId: Int,
	@ColumnInfo(name = "game_id") val gameId: Int,
	@ColumnInfo(name = "rating") val rating: Float,
	@ColumnInfo(name = "body") val body: String,
	@ColumnInfo(name = "completion") val completion: String,
	@ColumnInfo(name = "created_at") val createdAt: String? = null,
	@ColumnInfo(name = "updated_at") val updatedAt: String? = null
) {
	init {
		require(rating in 1.0f..5.0f) { "Rating must be between 1.0 and 5.0" }
	}

	// Helper for UI
	val completionEnum: CompletionType?
		get() = CompletionType.fromCode(completion)
}