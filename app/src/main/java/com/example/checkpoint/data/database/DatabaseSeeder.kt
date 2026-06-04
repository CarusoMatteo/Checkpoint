package com.example.checkpoint.data.database

import com.example.checkpoint.data.database.daos.AchievementDao
import com.example.checkpoint.data.database.daos.GameDao
import com.example.checkpoint.data.database.daos.ReviewDao
import com.example.checkpoint.data.database.daos.UserDao
import com.example.checkpoint.data.database.daos.UserAchievementDao
import com.example.checkpoint.data.database.entities.AchievementCategoryEntity
import com.example.checkpoint.data.database.entities.AchievementEntity
import com.example.checkpoint.data.database.entities.GameEntity
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserAchievementEntity
import com.example.checkpoint.data.database.entities.UserEntity
import java.time.Instant

/**
 * Seeds the database with sample data on first launch.
 * Called by AppModule via RoomDatabase.Callback.
 */
object DatabaseSeeder {

	val users = listOf(
		UserEntity(
			id = 1,
			username = "john_doe",
			email = "john@example.com",
			passwordHash = "hashed_pw_1",
			bio = "Lifelong gamer. I love RPGs and strategy games.",
			publicProfile = true,
			createdAt = "2024-01-10T10:00:00Z"
		),
		UserEntity(
			id = 2,
			username = "jane_smith",
			email = "jane@example.com",
			passwordHash = "hashed_pw_2",
			bio = "Speedrunner and completionist. Platinum on everything.",
			publicProfile = true,
			createdAt = "2024-02-14T09:30:00Z"
		),
		UserEntity(
			id = 3,
			username = "alex_j",
			email = "alex@example.com",
			passwordHash = "hashed_pw_3",
			bio = "Casual gamer. I prefer indie games.",
			publicProfile = false,
			createdAt = "2024-03-05T15:00:00Z"
		),
	)

	//Giochi fittizzi al quale associare le reviews
	val sampleGames = listOf(
		GameEntity(id = 1, igdbId = 1001),
		GameEntity(id = 2, igdbId = 1002),
		GameEntity(id = 3, igdbId = 1003)
	)

	// Recensioni agganciate agli utenti e ai giochi sopra definiti
	val sampleDbReviews = listOf(
		ReviewEntity(
			id = 1,
			userId = 1,// john_doe
			gameId = 1,
			rating = 9,
			body = "Great game with an engaging story and fun gameplay! I completed everything and loved every minute of it.",
			containsSpoilers = false,
			createdAt = Instant.now().toString()
		), ReviewEntity(
			id = 2,
			userId = 1,// john_doe
			gameId = 2,
			rating = 6,
			body = "The game was enjoyable, but I found the controls a bit clunky.",
			containsSpoilers = false,
			createdAt = Instant.now().toString()
		), ReviewEntity(
			id = 3,
			userId = 2,// jane_smith
			gameId = 1,
			rating = 10,
			body = "Absolutely loved it! The graphics and soundtrack were amazing.",
			containsSpoilers = false,
			createdAt = Instant.now().toString()
		), ReviewEntity(
			id = 4,
			userId = 3,// alex_j
			gameId = 3,
			rating = 5,
			body = "The game had potential, but it was plagued with bugs and performance issues.",
			containsSpoilers = false,
			createdAt = Instant.now().toString()
		)
	)

	val achievementCategories = listOf(
		AchievementCategoryEntity(
			id = 1, code = "SOCIAL", name = "Social", description = "Interact with the community"
		),
		AchievementCategoryEntity(
			id = 2,
			code = "EXPLORER",
			name = "Explorer",
			description = "Explore new genres and platforms"
		),
		AchievementCategoryEntity(
			id = 3,
			code = "COLLECTOR",
			name = "Collector",
			description = "Collect games and reviews"
		),
		AchievementCategoryEntity(
			id = 4, code = "CRITIC", name = "Critic", description = "Write quality reviews"
		),
	)

	val achievements = listOf(
		AchievementEntity(
			id = 1,
			categoryId = 2,
			code = "MULTI_PLATFORM",
			name = "Multiplatform Enjoyer",
			description = "Play on 5 different platforms",
			threshold = 5
		),
		AchievementEntity(
			id = 2,
			categoryId = 1,
			code = "NOTIFICATIONS",
			name = "Lots to Look Forward",
			description = "Activate notifications for 15 games",
			threshold = 15
		),
		AchievementEntity(
			id = 3,
			categoryId = 2,
			code = "VARIETY_GAMER",
			name = "Variety Gamer",
			description = "Play games from 5 different genres",
			threshold = 5
		),
		AchievementEntity(
			id = 4,
			categoryId = 3,
			code = "GAME_FINISHER",
			name = "Game Finisher",
			description = "Complete 10 games",
			threshold = 10
		),
		AchievementEntity(
			id = 5,
			categoryId = 4,
			code = "CRITIC",
			name = "Critic",
			description = "Write 5 reviews",
			threshold = 5
		),
		AchievementEntity(
			id = 6,
			categoryId = 2,
			code = "CONSISTENT",
			name = "Consistent Player",
			description = "Play 20 games of the same genre",
			threshold = 20
		),
	)

	val userAchievements = listOf(
		UserAchievementEntity(userId = 1, achievementId = 1, progress = 5, unlockedAt = null),
		UserAchievementEntity(
			userId = 1, achievementId = 2, progress = 15, unlockedAt = Instant.now().toString()
		),
		UserAchievementEntity(userId = 1, achievementId = 3, progress = 0, unlockedAt = null),
		UserAchievementEntity(userId = 1, achievementId = 4, progress = 7, unlockedAt = null),
		UserAchievementEntity(userId = 1, achievementId = 5, progress = 2, unlockedAt = null),
		UserAchievementEntity(userId = 1, achievementId = 6, progress = 3, unlockedAt = null),
	)

	suspend fun seed(
		userDao: UserDao,
		gameDao: GameDao,
		achievementDao: AchievementDao,
		userAchievementDao: UserAchievementDao,
		reviewDao: ReviewDao,
	) {
		// 1. Salva gli utenti
		users.forEach { userDao.upsert(it) }

		// 2. Salva i giochi
		sampleGames.forEach { gameDao.upsert(it) }

		// 3. Grazie al punto 2 Ora Room accetterà le recensioni senza problemi
		sampleDbReviews.forEach { reviewDao.upsert(it) }

		// 4. Salva gli Achievements
		achievementCategories.forEach { achievementDao.upsertCategory(it) }
		achievementDao.upsertAllAchievements(achievements)

		// 5. Salva i progressi degli Achievements
		userAchievements.forEach { userAchievementDao.upsert(it) }
	}
}