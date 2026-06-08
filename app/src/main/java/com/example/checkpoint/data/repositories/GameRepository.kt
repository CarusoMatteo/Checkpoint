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
import kotlinx.coroutines.flow.firstOrNull

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

	fun getAllGenresFromDb(): Flow<List<GenreEntity>> {
		return genreDao.getAll()
	}

	fun getAllPlatformsFromDb(): Flow<List<PlatformEntity>> {
		return platformDao.getAll()
	}

	suspend fun fetchGenresFromIgdb() {
		val apiGenres = igdbClient.getAllGenres()
		val genreEntities = apiGenres.map { networkGenre ->
			val existing = genreDao.getByIgdbId(networkGenre.id)
			GenreEntity(
				id = existing?.id ?: 0,
				igdbId = networkGenre.id,
				name = networkGenre.name ?: "Unknown"
			)
		}
		genreDao.upsertAll(genreEntities)
	}

	suspend fun fetchPlatformsFromIgdb() {
		val apiPlatforms = igdbClient.getAllPlatforms()
		val platformEntities = apiPlatforms.map { networkPlatform ->
			val existing = platformDao.getByIgdbId(networkPlatform.id)
			PlatformEntity(
				id = existing?.id ?: 0,
				igdbId = networkPlatform.id,
				name = networkPlatform.name ?: "Unknown",
				abbreviation = networkPlatform.abbreviation
			)
		}
		platformDao.upsertAll(platformEntities)
	}

	suspend fun getLocalEntityByIgdbId(igdbId: Int): GameEntity? = gameDao.getByIgdbId(igdbId)

	suspend fun saveGame(igdbId: Int): GameEntity {
		val existing = gameDao.getByIgdbId(igdbId)
		if (existing != null) return existing
		gameDao.upsert(GameEntity(igdbId = igdbId))
		return gameDao.getByIgdbId(igdbId)!!
	}

	suspend fun deleteGame(game: GameEntity) = gameDao.delete(game)

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

	suspend fun searchGamesWithFilters(
		query: String, genreIds: List<Int>, platformIds: List<Int>
	): List<Game> {
		val baseResults = searchGames(query)
		if (genreIds.isEmpty() && platformIds.isEmpty()) return baseResults

		val activeGenreNames = if (genreIds.isNotEmpty()) {
			genreDao.getAll().firstOrNull()?.filter { it.igdbId in genreIds }?.map { it.name }
				?: emptyList()
		} else emptyList()

		val activePlatformNames = if (platformIds.isNotEmpty()) {
			platformDao.getAll().firstOrNull()?.filter { it.igdbId in platformIds }?.map { it.name }
				?: emptyList()
		} else emptyList()

		return baseResults.filter { game ->
			val matchesGenre = if (activeGenreNames.isNotEmpty()) {
				game.genres.any { it in activeGenreNames }
			} else true

			val matchesPlatform = if (activePlatformNames.isNotEmpty()) {
				game.platforms.any { it in activePlatformNames }
			} else true

			matchesGenre && matchesPlatform
		}
	}

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

	suspend fun getSimilarGames(igdbId: Int): List<Game> = runCatching {
		val similarIds = igdbClient.getSimilarGamesIds(igdbId)
		if (similarIds.isEmpty()) return emptyList()

		val dtos = igdbClient.getGamesByIds(similarIds)
		dtos.map { dto ->
			val localGame = gameDao.getByIgdbId(dto.id)
			dto.toDomain(localId = localGame?.id ?: 0)
		}
	}.getOrElse { emptyList() }

	suspend fun getFranchiseGames(igdbId: Int): List<Game> {
		return try {
			val franchiseIds = igdbClient.getFranchiseGamesIds(igdbId)
			if (franchiseIds.isEmpty()) return emptyList()

			val dtos = igdbClient.getGamesByIds(franchiseIds)
			dtos.map { dto ->
				val localGame = gameDao.getByIgdbId(dto.id)
				dto.toDomain(localId = localGame?.id ?: 0)
			}
		} catch (e: Exception) {
			emptyList()
		}
	}

	suspend fun getComingSoonGames(limit: Int = 15): List<Game> = runCatching {
		val dtos = igdbClient.getComingSoonGames(limit)
		dtos.map { dto ->
			val localGame = gameDao.getByIgdbId(dto.id)
			dto.toDomain(localId = localGame?.id ?: 0)
		}
	}.getOrElse { emptyList() }

	private suspend fun cacheGameData(dto: IgdbGameDto) {
		dto.platforms?.let { platforms ->
			val entities = platforms.map { p ->
				val existingPlatform = platformDao.getByIgdbId(p.id)
				PlatformEntity(
					id = existingPlatform?.id ?: 0,
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
			val entities = genres.map { g ->
				val existingGenre = genreDao.getByIgdbId(g.id)
				GenreEntity(
					id = existingGenre?.id ?: 0, igdbId = g.id, name = g.name ?: "Unknown"
				)
			}
			genreDao.upsertAll(entities)
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