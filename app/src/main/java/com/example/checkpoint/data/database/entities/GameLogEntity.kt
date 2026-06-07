package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Game session logged by a user.
 *
 * [completionType] reflects the values of the [ReviewCompletion] enum in the domain:
 * "MAIN", "MAIN_AND_EXTRA", "COMPLETED", or null if not specified.
 *
 * [rating] ranges from 1 to 10 (TINYINT in the DB).
 * [hoursPlayed] is a decimal value (ex. 12.5 hours).
 */
@Entity(
	tableName = "game_logs", foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["user_id"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = GameEntity::class,
		parentColumns = ["id"],
		childColumns = ["game_id"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = PlatformEntity::class,
		parentColumns = ["id"],
		childColumns = ["platform_id"],
		onDelete = ForeignKey.SET_NULL
	)], indices = [Index("user_id"), Index("game_id"), Index("platform_id")]
)
data class GameLogEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,

	@ColumnInfo(name = "user_id") val userId: Int,

	@ColumnInfo(name = "game_id") val gameId: Int,

	@ColumnInfo(name = "platform_id") val platformId: Int? = null,

	/** Rating da 1 a 10 */
	@ColumnInfo(name = "rating") val rating: Int? = null,

	/** hours played, es. 12.50 */
	@ColumnInfo(name = "hours_played") val hoursPlayed: Double? = null,

	/** "MAIN", "MAIN_AND_EXTRA", "COMPLETED" o null */
	@ColumnInfo(name = "completion_type") val completionType: String? = null,

	@ColumnInfo(name = "started_at") val startedAt: String? = null,

	@ColumnInfo(name = "finished_at") val finishedAt: String? = null,

	@ColumnInfo(name = "created_at") val createdAt: String? = null,

	@ColumnInfo(name = "updated_at") val updatedAt: String? = null
)
