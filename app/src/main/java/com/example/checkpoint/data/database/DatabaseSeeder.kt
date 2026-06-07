package com.example.checkpoint.data.database

import com.example.checkpoint.data.database.entities.AchievementCategoryEntity
import com.example.checkpoint.data.database.entities.AchievementEntity
import com.example.checkpoint.data.database.entities.GameEntity
import com.example.checkpoint.data.database.entities.GameListEntity
import com.example.checkpoint.data.database.entities.GameLogEntity
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.ListEntryEntity
import com.example.checkpoint.data.database.entities.ReviewEntity
import com.example.checkpoint.data.database.entities.UserAchievementEntity
import com.example.checkpoint.data.database.entities.UserEntity
import com.example.checkpoint.data.database.entities.UserPreferredGenreEntity
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

	// Fictitious games to which to associate reviews
	val sampleGames = listOf(
		GameEntity(id = 1, igdbId = 22439),
		GameEntity(id = 2, igdbId = 1002),
		GameEntity(id = 3, igdbId = 1003),
		GameEntity(id = 4, igdbId = 42931),
		GameEntity(id = 5, igdbId = 1026),
		GameEntity(id = 6, igdbId = 6036)
	)

	// Reviews hooked to the users and games defined above
	val sampleDbReviews = listOf(
		ReviewEntity(
			id = 1,
			userId = 1,
			gameId = 1,
			rating = 9,
			body = "Great game with an engaging story and fun gameplay! I completed everything and loved every minute of it.",
			containsSpoilers = false,
			createdAt = Instant.now().toString()
		), ReviewEntity(
			id = 2,
			userId = 1,
			gameId = 2,
			rating = 6,
			body = "The game was enjoyable, but I found the controls a bit clunky.",
			containsSpoilers = false,
			createdAt = Instant.now().toString()
		), ReviewEntity(
			id = 3,
			userId = 2,
			gameId = 1,
			rating = 10,
			body = "Absolutely loved it! The graphics and soundtrack were amazing.",
			containsSpoilers = false,
			createdAt = Instant.now().toString()
		), ReviewEntity(
			id = 4,
			userId = 3,
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
		UserAchievementEntity(
			userId = 1, achievementId = 1, progress = 5, unlockedAt = null, isPinned = false
		),
		UserAchievementEntity(
			userId = 1,
			achievementId = 2,
			progress = 15,
			unlockedAt = Instant.now().toString(),
			isPinned = true
		),
		UserAchievementEntity(
			userId = 1, achievementId = 3, progress = 0, unlockedAt = null, isPinned = false
		),
		UserAchievementEntity(
			userId = 1, achievementId = 4, progress = 7, unlockedAt = null, isPinned = false
		),
		UserAchievementEntity(
			userId = 1, achievementId = 5, progress = 2, unlockedAt = null, isPinned = false
		),
		UserAchievementEntity(
			userId = 1, achievementId = 6, progress = 3, unlockedAt = null, isPinned = false
		),
	)

	val sampleLists = listOf(
		GameListEntity(id = 1, userId = 1, name = "Backlog", type = "BACKLOG", isPublic = true),
		GameListEntity(id = 2, userId = 1, name = "Saved", type = "SAVED", isPublic = true),
		GameListEntity(id = 3, userId = 1, name = "Favorites", type = "CUSTOM", isPublic = true)
	)

	val sampleListEntries = listOf(
		ListEntryEntity(listId = 1, gameId = 1, addedAt = "2026-05-01T12:00:00Z"), // BackLog
		ListEntryEntity(listId = 1, gameId = 2, addedAt = "2026-05-02T14:30:00Z"), // BackLog
		ListEntryEntity(listId = 2, gameId = 3, addedAt = "2026-05-03T09:15:00Z"), // Saved
		ListEntryEntity(listId = 1, gameId = 4, addedAt = "2026-06-04T10:00:00Z"), // Backlog
		ListEntryEntity(listId = 2, gameId = 5, addedAt = "2026-06-05T11:00:00Z"), // Saved
		ListEntryEntity(
			listId = 3, gameId = 6, addedAt = "2026-06-06T12:00:00Z"
		)  // Favorites (Custom)
	)

	val sampleGameLogs = listOf(
		GameLogEntity(
			id = 1,
			userId = 1,
			gameId = 1,
			rating = 9,
			hoursPlayed = 42.5,
			completionType = "COMPLETED",
			finishedAt = "2026-04-10"
		), GameLogEntity(
			id = 2,
			userId = 1,
			gameId = 2,
			rating = 8,
			hoursPlayed = 18.0,
			completionType = "MAIN",
			finishedAt = "2026-05-20"
		), GameLogEntity(
			id = 3,
			userId = 1,
			gameId = 3,
			hoursPlayed = 5.5,
			completionType = null,
			finishedAt = null
		)
	)

	// game genres
	val sampleGenres = listOf(
		GenreEntity(id = 1, igdbId = 12, name = "RPG"),
		GenreEntity(id = 2, igdbId = 5, name = "Shooter"),
		GenreEntity(id = 3, igdbId = 31, name = "Adventure"),
		GenreEntity(id = 4, igdbId = 15, name = "Strategy")
	)

	// Genres preferred by users
	val preferredGenres = listOf(
		UserPreferredGenreEntity(userId = 1, genreId = 1), // john_doe -> RPG
		UserPreferredGenreEntity(userId = 1, genreId = 4), // john_doe -> Strategy
		UserPreferredGenreEntity(userId = 2, genreId = 3)  // jane_smith -> Adventure
	)

	suspend fun seed(db: AppDatabase) {
		users.forEach { db.userDao().upsert(it) }

		sampleGames.forEach { db.gameDao().upsert(it) }

		sampleGenres.forEach { db.genreDao().upsert(it) }

		preferredGenres.forEach { db.userPreferredGenreDao().upsert(it) }

		sampleDbReviews.forEach { db.reviewDao().upsert(it) }

		achievementCategories.forEach { db.achievementDao().upsertCategory(it) }
		db.achievementDao().upsertAllAchievements(achievements)

		userAchievements.forEach { db.userAchievementDao().upsert(it) }

		sampleLists.forEach { db.gameListDao().upsert(it) }

		sampleListEntries.forEach { db.listEntryDao().upsert(it) }

		sampleGameLogs.forEach { db.gameLogDao().upsert(it) }
	}
}