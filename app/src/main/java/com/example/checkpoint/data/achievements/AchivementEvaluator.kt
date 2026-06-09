package com.example.checkpoint.data.achievements

import com.example.checkpoint.data.database.daos.AchievementMetricsDao
import com.example.checkpoint.data.repositories.AchievementRepository

/**
 * Maps each achievement code to a DB metric query and updates the progress
 * in the UserAchievements table via AchievementRepository.
 *
 * Called when AchievementsScreen opens.
 * All operations are suspend functions — safe to run in a coroutine.
 *
 * Adding a new achievement: define its code as a constant below, add a
 * branch in computeProgress(), and add the matching query to AchievementMetricsDao.
 *
 */
class AchievementEvaluator(
	private val metricsDao: AchievementMetricsDao,
	private val achievementRepository: AchievementRepository
) {
	companion object {
		// These codes MUST match DatabaseSeeder perfectly
		const val CODE_GAME_FINISHER = "GAME_FINISHER"
		const val CODE_CRITIC = "CRITIC"
		const val CODE_CONSISTENT = "CONSISTENT"
		const val CODE_VARIETY_GAMER = "VARIETY_GAMER"
	}

	/**
	 * Evaluates all known achievement codes for the given user and
	 * persists the updated progress to the DB.
	 */
	suspend fun evaluateAll(userId: Int) {
		val allAchievements = achievementRepository.getAllAchievementsOnce()

		for (achievement in allAchievements) {
			val newProgress = computeProgress(userId, achievement.code) ?: continue
			achievementRepository.updateProgress(
				userId = userId,
				achievementId = achievement.id,
				newProgress = newProgress,
				threshold = achievement.threshold
			)
		}
	}

	/**
	 * Returns the current real progress for a given code, or null if the
	 * code is unknown (skipped gracefully without crashing).
	 */
	private suspend fun computeProgress(userId: Int, code: String): Int? = when (code) {
		CODE_GAME_FINISHER -> metricsDao.countCompletedGames(userId)
		CODE_CRITIC -> metricsDao.countReviews(userId)
		CODE_CONSISTENT -> metricsDao.countBacklogGames(userId)
		CODE_VARIETY_GAMER -> metricsDao.countPreferredGenres(userId)
		else -> null
	}
}