package com.example.checkpoint.data.repositories

import com.example.checkpoint.data.database.daos.AchievementDao
import com.example.checkpoint.data.database.daos.GameListDao
import com.example.checkpoint.data.database.daos.ListEntryDao
import com.example.checkpoint.data.database.daos.UserAchievementDao
import com.example.checkpoint.data.database.entities.AchievementCategoryEntity
import com.example.checkpoint.data.database.entities.AchievementEntity
import com.example.checkpoint.data.database.entities.GameEntity
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.database.entities.ListEntryEntity
import com.example.checkpoint.data.database.entities.UserAchievementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.Instant

/**
 * Repository for game lists created by the user.
 */
class GameListRepository(
	private val gameListDao: GameListDao, private val listEntryDao: ListEntryDao
) {

	fun getListsForUser(userId: Int): Flow<List<GameListEntity>> =
		gameListDao.getListsForUser(userId)

	fun getListById(listId: Int): Flow<GameListEntity?> = gameListDao.getListById(listId)

	fun getGamesInList(listId: Int): Flow<List<GameEntity>> = listEntryDao.getGamesInList(listId)

	fun getListsContainingGame(gameId: Int): Flow<List<Int>> =
		listEntryDao.getListsContainingGame(gameId)

	/** Creates a new list. Returns the generated ID. */
	suspend fun createList(
		userId: Int, name: String, type: String = "CUSTOM", isPublic: Boolean = true
	): Long {
		val entity = GameListEntity(
			userId = userId,
			name = name,
			type = type,
			isPublic = isPublic,
			createdAt = Instant.now().toString()
		)
		return gameListDao.upsert(entity)
	}

	suspend fun deleteList(list: GameListEntity) = gameListDao.delete(list)

	/** Adds a game to the list (ignored if already present due to the UNIQUE constraint). */
	suspend fun addGameToList(listId: Int, gameId: Int) {
		listEntryDao.upsert(
			ListEntryEntity(
				listId = listId, gameId = gameId, addedAt = Instant.now().toString()
			)
		)
	}

	suspend fun removeGameFromList(listId: Int, gameId: Int) =
		listEntryDao.deleteByIds(listId, gameId)
}

/**
 * Domain model combining achievement details and user progress.
 */
data class AchievementWithProgress(
	val id: Int,
	val code: String,
	val name: String,
	val description: String?,
	val iconUrl: String?,
	val threshold: Int,
	val categoryId: Int,
	val progress: Int,
	val unlockedAt: String?,
	val isPinned: Boolean = false
) {
	val isUnlocked: Boolean get() = progress >= threshold
	val progressFraction: Float get() = (progress.toFloat() / threshold).coerceIn(0f, 1f)
}

class AchievementRepository(
	private val achievementDao: AchievementDao, private val userAchievementDao: UserAchievementDao
) {

	fun getAllAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

	fun getAllCategories(): Flow<List<AchievementCategoryEntity>> =
		achievementDao.getAllCategories()

	fun getAchievementsForUser(userId: Int): Flow<List<UserAchievementEntity>> =
		userAchievementDao.getAchievementsForUser(userId)

	fun getUnlockedAchievements(userId: Int): Flow<List<UserAchievementEntity>> =
		userAchievementDao.getUnlockedAchievements(userId)

	/**
	 * One-shot suspend version of getAllAchievements.
	 * Used by AchievementEvaluator to resolve code → id + threshold.
	 */
	suspend fun getAllAchievementsOnce(): List<AchievementEntity> =
		achievementDao.getAllAchievements().first()

	/**
	 * Updates the progress for a single achievement.
	 * If newProgress >= threshold, stamps unlocked_at (only the first time).
	 */
	suspend fun updateProgress(userId: Int, achievementId: Int, newProgress: Int, threshold: Int) {
		val current = userAchievementDao.getUserAchievement(userId, achievementId)
		// Preserve existing unlocked_at so we don't overwrite the original unlock timestamp
		val unlockedAt =
			current?.unlockedAt ?: if (newProgress >= threshold) Instant.now().toString() else null
		userAchievementDao.upsert(
			UserAchievementEntity(
				userId = userId,
				achievementId = achievementId,
				progress = newProgress,
				unlockedAt = unlockedAt,
				isPinned = current?.isPinned ?: false
			)
		)
	}

	/**
	 * Handles pinning directly to the local Room database.
	 */
	suspend fun togglePin(userId: Int, achievementId: Int) {
		val userAch = userAchievementDao.getUserAchievement(userId, achievementId)
		if (userAch != null && userAch.unlockedAt != null) {
			val currentPinnedCount = userAchievementDao.getPinnedCount(userId)
			if (userAch.isPinned) {
				userAchievementDao.upsert(userAch.copy(isPinned = false))
			} else if (currentPinnedCount < 3) {
				userAchievementDao.upsert(userAch.copy(isPinned = true))
			}
		}
	}

	suspend fun updatePin(
		userId: Int, achievementId: Int, isPinned: Boolean, progress: Int, unlockedAt: String?
	) {
		userAchievementDao.upsert(
			UserAchievementEntity(
				userId = userId,
				achievementId = achievementId,
				progress = progress,
				unlockedAt = unlockedAt,
				isPinned = isPinned
			)
		)
	}

	suspend fun seedAchievements(
		categories: List<AchievementCategoryEntity>, achievements: List<AchievementEntity>
	) {
		categories.forEach { achievementDao.upsertCategory(it) }
		achievementDao.upsertAllAchievements(achievements)
	}
}