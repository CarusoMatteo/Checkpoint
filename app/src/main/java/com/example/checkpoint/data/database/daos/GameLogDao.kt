package com.example.checkpoint.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.checkpoint.data.database.entities.GameLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameLogDao {

	/** All logs for a user, most recent first. */
	@Query("SELECT * FROM game_logs WHERE user_id = :userId ORDER BY updated_at DESC")
	fun getLogsForUser(userId: Int): Flow<List<GameLogEntity>>

	/** user log for a specific game (can be null if not yet logged in). */
	@Query("SELECT * FROM game_logs WHERE user_id = :userId AND game_id = :gameId LIMIT 1")
	fun getLogForGame(userId: Int, gameId: Int): Flow<GameLogEntity?>

	/** Completed logs (have an end date) for a user. */
	@Query(
		"""
        SELECT * FROM game_logs
        WHERE user_id = :userId AND finished_at IS NOT NULL
        ORDER BY finished_at DESC
        """
	)
	fun getCompletedGames(userId: Int): Flow<List<GameLogEntity>>

	/** Inserts or updates a log (use @Upsert: create if it doesn't exist, update otherwise). */
	@Upsert
	suspend fun upsert(log: GameLogEntity)

	@Delete
	suspend fun delete(log: GameLogEntity)
}
