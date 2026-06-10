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
		GameEntity(id = 1, igdbId = 22439),  // The Witcher 3
		GameEntity(id = 2, igdbId = 6036),   // The Last of Us
		GameEntity(id = 3, igdbId = 42931),  // Bloodborne
		GameEntity(id = 4, igdbId = 1026),  // Zelda a link to the past
		GameEntity(id = 5, igdbId = 23),   // System Shock
		GameEntity(id = 6, igdbId = 41),   // Deus Ex
		GameEntity(id = 7, igdbId = 20),   // BioShock
		GameEntity(id = 8, igdbId = 338),   // Half-Life 2
		GameEntity(id = 9, igdbId = 333),   // Quake
		GameEntity(id = 10, igdbId = 71),   // Portal
	)
	val sampleDbReviews = listOf(
		// john_doe (userId=1)
		ReviewEntity(
			id = 1,
			userId = 1,
			gameId = 1,
			rating = 4.5f,
			body = "Great game with an engaging story and fun gameplay! I completed everything and loved every minute of it.",
			completion = "COMPLETED",
			createdAt = Instant.now().toString()
		),
		ReviewEntity(
			id = 2,
			userId = 1,
			gameId = 2,
			rating = 3.0f,
			body = "The game was enjoyable, but I found the controls a bit clunky.",
			completion = "MAIN",
			createdAt = Instant.now().toString()
		),
		ReviewEntity(
			id = 5,
			userId = 1,
			gameId = 5,
			rating = 5.0f,
			body = "A masterpiece of immersive simulation. Tense atmosphere and incredible depth.",
			completion = "COMPLETED",
			createdAt = Instant.now().toString()
		),
		ReviewEntity(
			id = 6,
			userId = 1,
			gameId = 6,
			rating = 4.0f,
			body = "Deus Ex defined the genre. Unmatched freedom of choice and a gripping conspiracy story.",
			completion = "MAIN_AND_EXTRA",
			createdAt = Instant.now().toString()
		),
		ReviewEntity(
			id = 7,
			userId = 1,
			gameId = 7,
			rating = 4.5f,
			body = "BioShock blew me away. Rapture is one of the most memorable settings in gaming history.",
			completion = "MAIN",
			createdAt = Instant.now().toString()
		),
		// jane_smith (userId=2)
		ReviewEntity(
			id = 3,
			userId = 2,
			gameId = 1,
			rating = 5.0f,
			body = "Absolutely loved it! The graphics and soundtrack were amazing.",
			completion = "MAIN_AND_EXTRA",
			createdAt = Instant.now().toString()
		),
		ReviewEntity(
			id = 8,
			userId = 2,
			gameId = 8,
			rating = 4.5f,
			body = "Half-Life 2 is still a benchmark for storytelling through environment.",
			completion = "COMPLETED",
			createdAt = Instant.now().toString()
		),
		// alex_j (userId=3)
		ReviewEntity(
			id = 4,
			userId = 3,
			gameId = 3,
			rating = 2.5f,
			body = "The game had potential, but it was plagued with bugs and performance issues.",
			completion = "MAIN",
			createdAt = Instant.now().toString()
		),
	)

	val achievementCategories = listOf(
		AchievementCategoryEntity(
			id = 2, code = "EXPLORER", name = "Explorer", description = "Explore new genres"
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
			description = "100% complete 10 games",
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
			categoryId = 3,
			code = "CONSISTENT",
			name = "Consistent Player",
			description = "Add 20 games to your Backlog",
			threshold = 20
		),
	)

	val userAchievements = listOf(
		UserAchievementEntity(
			userId = 1, achievementId = 3, progress = 0, unlockedAt = null, isPinned = false
		),
		UserAchievementEntity(
			userId = 1, achievementId = 4, progress = 7, unlockedAt = null, isPinned = false
		),
		UserAchievementEntity(
			userId = 1,
			achievementId = 5,
			progress = 5,
			unlockedAt = "2026-06-01T12:00:00Z",
			isPinned = true
		),
		UserAchievementEntity(
			userId = 1, achievementId = 6, progress = 3, unlockedAt = null, isPinned = false
		),
	)

	val sampleLists = listOf(
		// john_doe (userId=1)
		GameListEntity(id = 1, userId = 1, name = "Backlog", type = "BACKLOG", isPublic = true),
		GameListEntity(id = 2, userId = 1, name = "Saved", type = "SAVED", isPublic = true),
		GameListEntity(id = 3, userId = 1, name = "Favorites", type = "CUSTOM", isPublic = true),
		// jane_smith (userId=2)
		GameListEntity(id = 4, userId = 2, name = "Backlog", type = "BACKLOG", isPublic = true),
		GameListEntity(id = 5, userId = 2, name = "Saved", type = "SAVED", isPublic = true),
		// alex_j (userId=3)
		GameListEntity(id = 6, userId = 3, name = "Backlog", type = "BACKLOG", isPublic = false),
		GameListEntity(id = 7, userId = 3, name = "Saved", type = "SAVED", isPublic = false),
	)

	val sampleListEntries = listOf(
		// john_doe — Backlog (listId=1)
		ListEntryEntity(listId = 1, gameId = 1, addedAt = "2026-05-01T12:00:00Z"),
		ListEntryEntity(listId = 1, gameId = 2, addedAt = "2026-05-02T14:30:00Z"),
		ListEntryEntity(listId = 1, gameId = 4, addedAt = "2026-06-04T10:00:00Z"),
		// john_doe — Saved (listId=2)
		ListEntryEntity(listId = 2, gameId = 3, addedAt = "2026-05-03T09:15:00Z"),
		ListEntryEntity(listId = 2, gameId = 5, addedAt = "2026-06-05T11:00:00Z"),
		// john_doe — Favorites (listId=3)
		ListEntryEntity(listId = 3, gameId = 6, addedAt = "2026-06-06T12:00:00Z"),

		// jane_smith — Backlog (listId=4)
		ListEntryEntity(listId = 4, gameId = 7, addedAt = "2026-05-10T10:00:00Z"),
		ListEntryEntity(listId = 4, gameId = 8, addedAt = "2026-05-11T11:00:00Z"),
		ListEntryEntity(listId = 4, gameId = 9, addedAt = "2026-05-12T12:00:00Z"),
		// jane_smith — Saved (listId=5)
		ListEntryEntity(listId = 5, gameId = 1, addedAt = "2026-05-13T09:00:00Z"),
		ListEntryEntity(listId = 5, gameId = 10, addedAt = "2026-05-14T08:00:00Z"),

		// alex_j — Backlog (listId=6)
		ListEntryEntity(listId = 6, gameId = 2, addedAt = "2026-06-01T10:00:00Z"),
		ListEntryEntity(listId = 6, gameId = 5, addedAt = "2026-06-02T11:00:00Z"),
		// alex_j — Saved (listId=7)
		ListEntryEntity(listId = 7, gameId = 6, addedAt = "2026-06-03T12:00:00Z"),
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
		),
		GameLogEntity(
			id = 2,
			userId = 1,
			gameId = 2,
			rating = 8,
			hoursPlayed = 18.0,
			completionType = "MAIN",
			finishedAt = "2026-05-20"
		),
		GameLogEntity(
			id = 3,
			userId = 1,
			gameId = 3,
			hoursPlayed = 5.5,
			completionType = null,
			finishedAt = null
		),
		GameLogEntity(
			id = 4,
			userId = 2,
			gameId = 7,
			rating = 9,
			hoursPlayed = 30.0,
			completionType = "COMPLETED",
			finishedAt = "2026-05-25"
		),
		GameLogEntity(
			id = 5,
			userId = 2,
			gameId = 8,
			rating = 10,
			hoursPlayed = 15.0,
			completionType = "COMPLETED",
			finishedAt = "2026-06-01"
		),
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