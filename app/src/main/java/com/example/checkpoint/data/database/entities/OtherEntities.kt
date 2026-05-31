package com.example.checkpoint.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Lists and list entries

@Entity(
	tableName = "lists", foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["user_id"],
		onDelete = ForeignKey.CASCADE
	)], indices = [Index("user_id")]
)
data class GameListEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
	@ColumnInfo(name = "user_id") val userId: Int,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "type") val type: String = "CUSTOM",
	@ColumnInfo(name = "is_public") val isPublic: Boolean = true,
	@ColumnInfo(name = "created_at") val createdAt: String? = null
)

@Entity(
	tableName = "list_entries", foreignKeys = [ForeignKey(
		entity = GameListEntity::class,
		parentColumns = ["id"],
		childColumns = ["list_id"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = GameEntity::class,
		parentColumns = ["id"],
		childColumns = ["game_id"],
		onDelete = ForeignKey.CASCADE
	)], indices = [Index(value = ["list_id", "game_id"], unique = true), Index("game_id")]
)
data class ListEntryEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
	@ColumnInfo(name = "list_id") val listId: Int,
	@ColumnInfo(name = "game_id") val gameId: Int,
	@ColumnInfo(name = "added_at") val addedAt: String? = null
)

// Achievements

@Entity(tableName = "achievement_categories")
data class AchievementCategoryEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
	@ColumnInfo(name = "code") val code: String,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "description") val description: String? = null,
	@ColumnInfo(name = "icon_url") val iconUrl: String? = null
)

@Entity(
	tableName = "achievements", foreignKeys = [ForeignKey(
		entity = AchievementCategoryEntity::class,
		parentColumns = ["id"],
		childColumns = ["category_id"],
		onDelete = ForeignKey.CASCADE
	)], indices = [Index("category_id")]
)
data class AchievementEntity(
	@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
	@ColumnInfo(name = "category_id") val categoryId: Int,
	@ColumnInfo(name = "code") val code: String,
	@ColumnInfo(name = "name") val name: String,
	@ColumnInfo(name = "description") val description: String? = null,
	@ColumnInfo(name = "icon_url") val iconUrl: String? = null,
	@ColumnInfo(name = "threshold") val threshold: Int
)

@Entity(
	tableName = "user_achievements",
	primaryKeys = ["user_id", "achievement_id"],
	foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["user_id"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = AchievementEntity::class,
		parentColumns = ["id"],
		childColumns = ["achievement_id"],
		onDelete = ForeignKey.CASCADE
	)],
	indices = [Index("achievement_id")]
)
data class UserAchievementEntity(
	@ColumnInfo(name = "user_id") val userId: Int,
	@ColumnInfo(name = "achievement_id") val achievementId: Int,
	@ColumnInfo(name = "progress") val progress: Int = 0,
	@ColumnInfo(name = "unlocked_at") val unlockedAt: String? = null
)

// Many-to-many relationships

@Entity(
	tableName = "game_platforms",
	primaryKeys = ["game_id", "platform_id"],
	foreignKeys = [ForeignKey(
		entity = GameEntity::class,
		parentColumns = ["id"],
		childColumns = ["game_id"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = PlatformEntity::class,
		parentColumns = ["id"],
		childColumns = ["platform_id"],
		onDelete = ForeignKey.CASCADE
	)],
	indices = [Index("platform_id")]
)
data class GamePlatformEntity(
	@ColumnInfo(name = "game_id") val gameId: Int,
	@ColumnInfo(name = "platform_id") val platformId: Int,
	@ColumnInfo(name = "release_date") val releaseDate: String? = null
)

@Entity(
	tableName = "user_preferred_genres",
	primaryKeys = ["user_id", "genre_id"],
	foreignKeys = [ForeignKey(
		entity = UserEntity::class,
		parentColumns = ["id"],
		childColumns = ["user_id"],
		onDelete = ForeignKey.CASCADE
	), ForeignKey(
		entity = GenreEntity::class,
		parentColumns = ["id"],
		childColumns = ["genre_id"],
		onDelete = ForeignKey.CASCADE
	)],
	indices = [Index("genre_id")]
)
data class UserPreferredGenreEntity(
	@ColumnInfo(name = "user_id") val userId: Int, @ColumnInfo(name = "genre_id") val genreId: Int
)