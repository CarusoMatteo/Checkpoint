package com.example.checkpoint.data.repositories

import com.example.checkpoint.data.database.daos.GameDao
import com.example.checkpoint.data.database.daos.GamePlatformDao
import com.example.checkpoint.data.database.daos.GenreDao
import com.example.checkpoint.data.database.daos.PlatformDao
import com.example.checkpoint.data.database.entities.GameEntity
import com.example.checkpoint.data.database.entities.GamePlatformEntity
import com.example.checkpoint.data.database.entities.GenreEntity
import com.example.checkpoint.data.database.entities.PlatformEntity
import com.example.checkpoint.data.remote.dto.IgdbGameDto
import com.example.checkpoint.data.remote.igdb.IgdbClient
import com.example.checkpoint.data.remote.igdb.IgdbImageUrl
import kotlinx.coroutines.flow.Flow

data class Game(
	val id: Int,          // Local Room ID (0 if not saved yet)
	val igdbId: Int,
	val name: String,
	val summary: String?,
	val coverUrl: String?,
	val genres: List<String>,
	val platforms: List<String>,
	val developer: String?,
	val publisher: String?,
	val firstReleaseDate: Long?,
	val totalRating: Double?,
	val totalRatingCount: Int?,
)

class GameRepository(
	private val gameDao: GameDao,
	private val platformDao: PlatformDao,
	private val genreDao: GenreDao,
	private val gamePlatformDao: GamePlatformDao,
	private val igdbClient: IgdbClient,
) {
	val savedIgdbIds: Flow<List<Int>> = gameDao.getAllIgdbIds()
	val savedGames: Flow<List<GameEntity>> = gameDao.getAll()

	/** Retrieves the local entity given an igdbId (without creating it). */
	suspend fun getLocalEntityByIgdbId(igdbId: Int): GameEntity? = gameDao.getByIgdbId(igdbId)

	/** Saves the game locally (upsert). Returns the saved entity. */
	suspend fun saveGame(igdbId: Int): GameEntity {
		val existing = gameDao.getByIgdbId(igdbId)
		if (existing != null) return existing
		gameDao.upsert(GameEntity(igdbId = igdbId))
		return gameDao.getByIgdbId(igdbId)!!
	}

	suspend fun deleteGame(game: GameEntity) = gameDao.delete(game)

	/** Full details of a game from IGDB; updates the local cache. */
	suspend fun fetchGameDetails(igdbId: Int): Game? = runCatching {
		val dto = igdbClient.getGameById(igdbId) ?: return null
		cacheGameData(dto)
		dto.toDomain(localId = gameDao.getByIgdbId(igdbId)?.id ?: 0)
	}.getOrNull()

	suspend fun searchGames(query: String): List<Game> = runCatching {
		igdbClient.searchGames(query).map { dto ->
			dto.toDomain(localId = gameDao.getByIgdbId(dto.id)?.id ?: 0)
		}
	}.getOrElse { emptyList() }

	suspend fun getPopularGames(limit: Int = 20, offset: Int = 0): List<Game> = runCatching {
		igdbClient.getPopularGames(limit, offset).map { dto ->
			dto.toDomain(localId = gameDao.getByIgdbId(dto.id)?.id ?: 0)
		}
	}.getOrElse { emptyList() }

	suspend fun getRecentReleases(limit: Int = 20): List<Game> = runCatching {
		igdbClient.getRecentReleases(limit).map { dto ->
			dto.toDomain(localId = gameDao.getByIgdbId(dto.id)?.id ?: 0)
		}
	}.getOrElse { emptyList() }

	suspend fun getGamesByGenre(genreIgdbId: Int, limit: Int = 20): List<Game> = runCatching {
		igdbClient.getGamesByGenre(genreIgdbId, limit).map { dto ->
			dto.toDomain(localId = gameDao.getByIgdbId(dto.id)?.id ?: 0)
		}
	}.getOrElse { emptyList() }

	/**
	 * Caches game metadata (platforms and genres) from the IGDB DTO into the local database.
	 *
	 * This helper method extracts and updates platform details, updates the many-to-many
	 * cross-reference join table if the game already exists locally, and inserts or updates
	 * the associated genre entries.
	 *
	 * @param dto The [IgdbGameDto] containing the game metadata to be cached.
	 */
	private suspend fun cacheGameData(dto: IgdbGameDto) {
		dto.platforms?.let { platforms ->
			val entities = platforms.map { p ->
				PlatformEntity(
					igdbId = p.id,
					name = p.name ?: "Unknown",
					abbreviation = p.abbreviation,
					logoUrl = p.platformLogo?.imageId?.let { IgdbImageUrl.coverSmall(it) },
				)
			}
			platformDao.upsertAll(entities)

			val localGame = gameDao.getByIgdbId(dto.id)
			localGame?.let { game ->
				entities.forEach { platform ->
					platformDao.getByIgdbId(platform.igdbId)?.let { saved ->
						gamePlatformDao.upsert(
							GamePlatformEntity(
								gameId = game.id, platformId = saved.id
							)
						)
					}
				}
			}
		}

		dto.genres?.let { genres ->
			genreDao.upsertAll(genres.map { g ->
				GenreEntity(
					igdbId = g.id, name = g.name ?: "Unknown"
				)
			})
		}
	}
}

fun IgdbGameDto.toDomain(localId: Int = 0): Game = Game(
	id = localId,
	igdbId = id,
	name = name ?: "Unknown",
	summary = summary,
	coverUrl = cover?.imageId?.let { IgdbImageUrl.coverBig(it) },
	genres = genres?.mapNotNull { it.name } ?: emptyList(),
	platforms = platforms?.mapNotNull { it.name } ?: emptyList(),
	developer = involvedCompanies?.firstOrNull { it.developer == true }?.company?.name,
	publisher = involvedCompanies?.firstOrNull { it.publisher == true }?.company?.name,
	firstReleaseDate = firstReleaseDate,
	totalRating = totalRating,
	totalRatingCount = totalRatingCount,
)