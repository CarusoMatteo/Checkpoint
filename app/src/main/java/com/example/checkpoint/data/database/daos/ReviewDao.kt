package com.example.checkpoint.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.checkpoint.data.database.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

	/** All reviews for a game, ordered by most recent. */
	@Query("SELECT * FROM reviews WHERE game_id = :gameId ORDER BY created_at DESC")
	fun getReviewsForGame(gameId: Int): Flow<List<ReviewEntity>>

	/** All reviews written by a user. */
	@Query("SELECT * FROM reviews WHERE user_id = :userId ORDER BY created_at DESC")
	fun getReviewsByUser(userId: Int): Flow<List<ReviewEntity>>

	/** A user's review for a specific game (at most one). */
	@Query("SELECT * FROM reviews WHERE user_id = :userId AND game_id = :gameId LIMIT 1")
	fun getReviewForGame(userId: Int, gameId: Int): Flow<ReviewEntity?>

	/** Average rating of a game (e.g., 7.3). */
	@Query("SELECT AVG(rating) FROM reviews WHERE game_id = :gameId")
	fun getAverageRating(gameId: Int): Flow<Double?>

	/** Number of reviews for a game. */
	@Query("SELECT COUNT(*) FROM reviews WHERE game_id = :gameId")
	fun getReviewCount(gameId: Int): Flow<Int>

	@Upsert
	suspend fun upsert(review: ReviewEntity)

	@Delete
	suspend fun delete(review: ReviewEntity)
}