package com.example.checkpoint.data.repositories

import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * Repository for game reviews.
 */
class ReviewRepository(
	private val reviewDao: ReviewDao
) {

	fun getReviewsForGame(gameId: Int): Flow<List<ReviewEntity>> =
		reviewDao.getReviewsForGame(gameId)

	fun getReviewsByUser(userId: Int): Flow<List<ReviewEntity>> = reviewDao.getReviewsByUser(userId)

	fun getReviewForGame(userId: Int, gameId: Int): Flow<ReviewEntity?> =
		reviewDao.getReviewForGame(userId, gameId)

	fun getAverageRating(gameId: Int): Flow<Double?> = reviewDao.getAverageRating(gameId)

	fun getReviewCount(gameId: Int): Flow<Int> = reviewDao.getReviewCount(gameId)

	/**
	 * Creates or updates a user's review for a game.
	 * [rating] must be between 1 and 10.
	 */
	suspend fun upsertReview(
		userId: Int,
		gameId: Int,
		rating: Int,
		body: String,
		containsSpoilers: Boolean = false,
		existingId: Int = 0
	) {
		require(rating in 1..10) { "Rating must be between 1 and 10" }

		val now = Instant.now().toString()
		val entity = ReviewEntity(
			id = existingId,
			userId = userId,
			gameId = gameId,
			rating = rating,
			body = body,
			containsSpoilers = containsSpoilers,
			createdAt = if (existingId == 0) now else null,
			updatedAt = now
		)
		reviewDao.upsert(entity)
	}

	suspend fun deleteReview(review: ReviewEntity) = reviewDao.delete(review)
}