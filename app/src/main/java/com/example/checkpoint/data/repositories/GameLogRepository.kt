package com.example.checkpoint.data.repositories

import com.example.checkpoint.data.database.daos.GameLogDao
import com.example.checkpoint.data.database.entities.GameLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Completion type aligned with the [ReviewCompletion] enum in the existing UI domain.
 */
enum class CompletionType(val code: String, val displayName: String) {
	MAIN("MAIN", "Main"),
	MAIN_AND_EXTRA("MAIN_AND_EXTRA", "Main + Extras"),
	COMPLETED("COMPLETED", "100% completed");

	companion object {
		fun fromCode(code: String?): CompletionType? = entries.firstOrNull { it.code == code }
	}
}

/**
 * Repository for the user's game logs.
 * Handles the creation, updating, and deletion of game sessions.
 */
class GameLogRepository(
	private val gameLogDao: GameLogDao
) {

	fun getLogsForUser(userId: Int): Flow<List<GameLogEntity>> = gameLogDao.getLogsForUser(userId)

	fun getLogForGame(userId: Int, gameId: Int): Flow<GameLogEntity?> =
		gameLogDao.getLogForGame(userId, gameId)

	fun getCompletedGames(userId: Int): Flow<List<GameLogEntity>> =
		gameLogDao.getCompletedGames(userId)

	/**
	 * Creates or updates a game log.
	 */
	suspend fun upsertLog(
		userId: Int,
		gameId: Int,
		platformId: Int? = null,
		rating: Int? = null,
		hoursPlayed: Double? = null,
		completionType: CompletionType? = null,
		startedAt: LocalDate? = null,
		finishedAt: LocalDate? = null,
		existingId: Int = 0
	) {
		val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
		val now = Instant.now().toString()

		val entity = GameLogEntity(
			id = existingId,
			userId = userId,
			gameId = gameId,
			platformId = platformId,
			rating = rating,
			hoursPlayed = hoursPlayed,
			completionType = completionType?.code,
			startedAt = startedAt?.format(dateFormatter),
			finishedAt = finishedAt?.format(dateFormatter),
			createdAt = if (existingId == 0) now else null,
			updatedAt = now
		)
		gameLogDao.upsert(entity)
	}

	suspend fun deleteLog(log: GameLogEntity) = gameLogDao.delete(log)
}