package com.example.checkpoint.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.checkpoint.data.database.entities.AchievementCategoryEntity
import com.example.checkpoint.data.database.entities.AchievementEntity
import com.example.checkpoint.data.database.entities.GameEntity
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.database.entities.GamePlatformEntity
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.ListEntryEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import com.example.checkpoint.data.database.entities.UserAchievementEntity
import com.example.checkpoint.data.database.entities.UserPreferredGenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
	@Query("SELECT * FROM games")
	fun getAll(): Flow<List<GameEntity>>

	@Query("SELECT * FROM games WHERE igdb_id = :igdbId LIMIT 1")
	suspend fun getByIgdbId(igdbId: Int): GameEntity?

	@Query("SELECT igdb_id FROM games")
	fun getAllIgdbIds(): Flow<List<Int>>

	@Upsert
	suspend fun upsert(game: GameEntity)

	@Upsert
	suspend fun upsertAll(games: List<GameEntity>)

	@Delete
	suspend fun delete(game: GameEntity)
}

@Dao
interface PlatformDao {
	@Query("SELECT * FROM platforms ORDER BY name ASC")
	fun getAll(): Flow<List<PlatformEntity>>

	@Query("SELECT * FROM platforms WHERE igdb_id = :igdbId LIMIT 1")
	suspend fun getByIgdbId(igdbId: Int): PlatformEntity?

	@Query(
		"""
        SELECT p.* FROM platforms p
        INNER JOIN game_platforms gp ON gp.platform_id = p.id
        WHERE gp.game_id = :gameId ORDER BY p.name ASC
    """
	)
	fun getPlatformsForGame(gameId: Int): Flow<List<PlatformEntity>>

	@Upsert
	suspend fun upsert(platform: PlatformEntity)

	@Upsert
	suspend fun upsertAll(platforms: List<PlatformEntity>)
}

@Dao
interface GenreDao {
	@Query("SELECT * FROM genres ORDER BY name ASC")
	fun getAll(): Flow<List<GenreEntity>>

	@Query("SELECT * FROM genres WHERE igdb_id = :igdbId LIMIT 1")
	suspend fun getByIgdbId(igdbId: Int): GenreEntity?

	@Query(
		"""
        SELECT g.* FROM genres g
        INNER JOIN user_preferred_genres upg ON g.id = upg.genre_id
        WHERE upg.user_id = :userId
        ORDER BY g.name ASC
    """
	)
	fun getPreferredGenresForUser(userId: Int): Flow<List<GenreEntity>>

	@Upsert
	suspend fun upsertAll(genres: List<GenreEntity>)

	@Upsert
	suspend fun upsert(genre: GenreEntity): Long
}

@Dao
interface GameListDao {
	@Query("SELECT * FROM lists WHERE user_id = :userId ORDER BY created_at DESC")
	fun getListsForUser(userId: Int): Flow<List<GameListEntity>>

	@Query("SELECT * FROM lists WHERE id = :listId LIMIT 1")
	fun getListById(listId: Int): Flow<GameListEntity?>

	@Upsert
	suspend fun upsert(list: GameListEntity): Long

	@Delete
	suspend fun delete(list: GameListEntity)
}

@Dao
interface ListEntryDao {
	@Query(
		"""
        SELECT g.* FROM games g
        INNER JOIN list_entries le ON le.game_id = g.id
        WHERE le.list_id = :listId ORDER BY le.added_at DESC
    """
	)
	fun getGamesInList(listId: Int): Flow<List<GameEntity>>

	@Query("SELECT list_id FROM list_entries WHERE game_id = :gameId")
	fun getListsContainingGame(gameId: Int): Flow<List<Int>>

	@Upsert
	suspend fun upsert(entry: ListEntryEntity)

	@Delete
	suspend fun delete(entry: ListEntryEntity)

	@Query("DELETE FROM list_entries WHERE list_id = :listId AND game_id = :gameId")
	suspend fun deleteByIds(listId: Int, gameId: Int)
}

@Dao
interface AchievementDao {
	@Query("SELECT * FROM achievement_categories ORDER BY name ASC")
	fun getAllCategories(): Flow<List<AchievementCategoryEntity>>

	@Query("SELECT * FROM achievements ORDER BY threshold ASC")
	fun getAllAchievements(): Flow<List<AchievementEntity>>

	@Upsert
	suspend fun upsertCategory(category: AchievementCategoryEntity): Long

	@Upsert
	suspend fun upsertAchievement(achievement: AchievementEntity): Long

	@Upsert
	suspend fun upsertAllAchievements(achievements: List<AchievementEntity>)
}

@Dao
interface UserAchievementDao {
	@Query("SELECT * FROM user_achievements WHERE user_id = :userId")
	fun getAchievementsForUser(userId: Int): Flow<List<UserAchievementEntity>>

	@Query("SELECT * FROM user_achievements WHERE user_id = :userId AND unlocked_at IS NOT NULL")
	fun getUnlockedAchievements(userId: Int): Flow<List<UserAchievementEntity>>

	@Query("SELECT * FROM user_achievements WHERE user_id = :userId AND achievement_id = :achievementId LIMIT 1")
	suspend fun getUserAchievement(userId: Int, achievementId: Int): UserAchievementEntity?

	@Query("SELECT COUNT(*) FROM user_achievements WHERE user_id = :userId AND is_pinned = 1")
	suspend fun getPinnedCount(userId: Int): Int

	@Upsert
	suspend fun upsert(userAchievement: UserAchievementEntity)
}

@Dao
interface GamePlatformDao {
	@Upsert
	suspend fun upsert(gamePlatform: GamePlatformEntity)

	@Upsert
	suspend fun upsertAll(gamePlatforms: List<GamePlatformEntity>)

	@Delete
	suspend fun delete(gamePlatform: GamePlatformEntity)
}

@Dao
interface UserPreferredGenreDao {
	@Upsert
	suspend fun upsert(entry: UserPreferredGenreEntity)

	@Query("DELETE FROM user_preferred_genres WHERE user_id = :userId AND genre_id = :genreId")
	suspend fun delete(userId: Int, genreId: Int)

	@Query("DELETE FROM user_preferred_genres WHERE user_id = :userId")
	suspend fun deleteAllForUser(userId: Int)
}

// ─── Achievement metric queries

@Dao
interface AchievementMetricsDao {

	/** Count only games in the user's BACKLOG list */
	@Query(
		"""
        SELECT COUNT(le.game_id)
        FROM list_entries le
        INNER JOIN lists l ON l.id = le.list_id
        WHERE l.user_id = :userId AND l.type = 'BACKLOG'
    """
	)
	suspend fun countBacklogGames(userId: Int): Int

	/** Count the number of total reviews written */
	@Query("SELECT COUNT(*) FROM reviews WHERE user_id = :userId")
	suspend fun countReviews(userId: Int): Int

	/** Count games with a 100% completed review ('COMPLETED') */
	@Query("SELECT COUNT(*) FROM reviews WHERE user_id = :userId AND completion = 'COMPLETED'")
	suspend fun countCompletedGames(userId: Int): Int

	/** Count the number of favorite genres */
	@Query("SELECT COUNT(*) FROM user_preferred_genres WHERE user_id = :userId")
	suspend fun countPreferredGenres(userId: Int): Int
}