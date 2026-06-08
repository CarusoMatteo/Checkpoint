package com.example.checkpoint.data.remote.igdb

import android.util.Log
import com.example.checkpoint.data.remote.dto.IgdbGameDto
import com.example.checkpoint.data.remote.dto.IgdbGenreDto
import com.example.checkpoint.data.remote.dto.IgdbPlatformDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val TAG = "IgdbClient"

/**
 * Utility to build IGDB image URLs from an imageId.
 */
object IgdbImageUrl {
	fun coverBig(imageId: String) =
		"https://images.igdb.com/igdb/image/upload/t_cover_big/$imageId.jpg"

	fun coverSmall(imageId: String) =
		"https://images.igdb.com/igdb/image/upload/t_cover_small/$imageId.jpg"

}

/**
 * Client for the IGDB API (v4) via Ktor.
 *
 * IGDB requires Twitch OAuth 2.0 authentication:
 * - [clientId]: Client ID obtained from the Twitch Developer Console
 * - [accessToken]: Bearer token obtained via client_credentials
 *
 * Both are injected via Koin (read from BuildConfig).
 *
 * Example of an IGDB query (Apicalypse language):
 * ```
 * fields name, cover.image_id, genres.name;
 * where id = 1942;
 * ```
 */
class IgdbClient(
	private val httpClient: HttpClient,
	private val clientId: String,
	private val accessToken: String
) {

	private val baseUrl = "https://api.igdb.com/v4"

	/**
	 * Retrieves the full details of a game given its IGDB ID.
	 */
	suspend fun getGameById(igdbId: Int): IgdbGameDto? {
		val results: List<IgdbGameDto> = query(
			endpoint = "games", body = """
                fields name, summary, storyline,
                       cover.image_id,
                       genres.id, genres.name,
                       platforms.id, platforms.name, platforms.abbreviation,
                       platforms.platform_logo.image_id,
                       involved_companies.company.name,
                       involved_companies.developer,
                       involved_companies.publisher,
                       first_release_date,
                       total_rating, total_rating_count, aggregated_rating;
                where id = $igdbId;
                limit 1;
            """.trimIndent()
		)
		return results.firstOrNull()
	}

	/**
	 * Retrieves the list of similar game IDs for a given game.
	 */
	suspend fun getSimilarGamesIds(igdbId: Int): List<Int> {
		return runCatching {
			val response = query<List<IgdbGameDto>>(
				endpoint = "games", body = "fields similar_games; where id = $igdbId;"
			)
			response.firstOrNull()?.similarGames ?: emptyList()
		}.getOrElse { emptyList() }
	}

	/**
	 * Retrieve IDs of games belonging to the same franchise/collection.
	 */
	suspend fun getFranchiseGamesIds(igdbId: Int): List<Int> {
		return try {
			// I request the franchises and collections where the game is present
			val response = query<List<IgdbGameDto>>(
				endpoint = "games", body = """
                fields collections, franchises;
                where id = $igdbId;
            """.trimIndent()
			)

			val gameDto = response.firstOrNull()
			val relatedIds = mutableListOf<Int>()

			gameDto?.collections?.let { relatedIds.addAll(it) }
			gameDto?.franchises?.let { relatedIds.addAll(it) }

			if (relatedIds.isEmpty()) {
				Log.d(
					TAG,
					"getFranchiseGamesIds: igdbId=$igdbId does not belong to any collection or franchise"
				)
				return emptyList()
			}

			Log.d(
				TAG,
				"getFranchiseGamesIds: igdbId=$igdbId belongs to the collections/franchises: $relatedIds"
			)

			//I build the conditions
			// use of both franchises and collections for future-proofing (collection is becoming deprecated)
			val conditions = mutableListOf<String>()
			if (!gameDto?.collections.isNullOrEmpty()) {
				conditions.add("collections = (${gameDto.collections.joinToString(",")})")
			}
			if (!gameDto?.franchises.isNullOrEmpty()) {
				conditions.add("franchises = (${gameDto.franchises.joinToString(",")})")
			}

			val whereClause = conditions.joinToString(" | ")

			// Catch up on games that match conditions
			val gamesResponse = query<List<IgdbGameDto>>(
				endpoint = "games", body = """
                fields id;
                where $whereClause;
                limit 15;
            """.trimIndent()
			)

			//I exclude the current game
			val franchiseIds = gamesResponse.map { it.id }.filter { it != igdbId }

			Log.d(TAG, "getFranchiseGamesIds: Found ${franchiseIds.size} other related games")

			franchiseIds
		} catch (e: Exception) {
			Log.e(TAG, "getFranchiseGamesIds: error for igdbId=$igdbId - ${e.message}", e)
			emptyList()
		}
	}

	/**
	 * Recover upcoming games (with future release date).
	 */
	suspend fun getComingSoonGames(limit: Int = 15): List<IgdbGameDto> {
		//Current timestamp
		val currentTimestamp = System.currentTimeMillis() / 1000
		return query(
			endpoint = "games", body = """
                fields name, summary,
                       cover.image_id,
                       genres.name,
                       platforms.name,
                       first_release_date,
                       total_rating,
                       total_rating_count;
                where first_release_date > $currentTimestamp & cover != null;
                sort first_release_date asc;
                limit $limit;
            """.trimIndent()
		)
	}

	/**
	 * Retrieves the details of multiple games in a single request.
	 */
	suspend fun getGamesByIds(igdbIds: List<Int>): List<IgdbGameDto> {
		if (igdbIds.isEmpty()) return emptyList()
		val idList = igdbIds.joinToString(",")
		return query(
			endpoint = "games", body = """
                fields name, summary,
                       cover.image_id,
                       genres.id, genres.name,
                       platforms.id, platforms.name, platforms.abbreviation,
                       first_release_date,
                       total_rating;
                where id = ($idList);
                limit ${igdbIds.size};
            """.trimIndent()
		)
	}


	/**
	 * Searches for games by name.
	 * [limit] maximum 500 per request (IGDB limit).
	 */
	suspend fun searchGames(query: String, limit: Int = 20): List<IgdbGameDto> {
		return query(
			endpoint = "games", body = """
                fields name, summary,
                       cover.image_id,
                       genres.name,
                       platforms.name,
                       involved_companies.company.name,
                       involved_companies.developer,
                       involved_companies.publisher,
                       first_release_date,
                       total_rating;
                search "$query";
                limit $limit;
            """.trimIndent()
		)
	}

	/**
	 * Popular games (sorted by rating, with a minimum number of votes).
	 */
	suspend fun getPopularGames(limit: Int = 20, offset: Int = 0): List<IgdbGameDto> {
		return query(
			endpoint = "games", body = """
                fields name, summary,
                       cover.image_id,
                       genres.name,
                       platforms.name,
                       first_release_date,
                       total_rating, total_rating_count;
                where total_rating_count > 100 & cover != null;
                sort total_rating desc;
                limit $limit;
                offset $offset;
            """.trimIndent()
		)
	}

	/**
	 * Recent releases (last 3 months, sorted by date).
	 */
	suspend fun getRecentReleases(limit: Int = 20): List<IgdbGameDto> {
		val threeMonthsAgo = System.currentTimeMillis() / 1000 - (90L * 24 * 60 * 60)
		val now = System.currentTimeMillis() / 1000
		return query(
			endpoint = "games", body = """
                fields name, summary,
                       cover.image_id,
                       genres.name,
                       platforms.name,
                       first_release_date,
                       total_rating;
                where first_release_date >= $threeMonthsAgo
                    & first_release_date <= $now
                    & cover != null;
                sort first_release_date desc;
                limit $limit;
            """.trimIndent()
		)
	}

	/**
	 * Games of a specific genre (by IGDB genre ID).
	 */
	suspend fun getGamesByGenre(genreIgdbId: Int, limit: Int = 20): List<IgdbGameDto> {
		return query(
			endpoint = "games", body = """
                fields name, summary,
                       cover.image_id,
                       genres.name,
                       platforms.name,
                       first_release_date,
                       total_rating;
                where genres = [$genreIgdbId] & cover != null & total_rating_count > 20;
                sort total_rating desc;
                limit $limit;
            """.trimIndent()
		)
	}

	/**
	 * Retrieves the full list of genres from IGDB.
	 * Max 500 per request.
	 */
	suspend fun getAllGenres(): List<IgdbGenreDto> {
		return runCatching {
			query<List<IgdbGenreDto>>(
				endpoint = "genres", body = """
					fields name;
					limit 500;
				""".trimIndent()
			)
		}.getOrElse {
			Log.e(TAG, "Error in retrieval of genres: ${it.message}")
			emptyList()
		}
	}

	/**
	 * Retrieves the full list of platforms from IGDB.
	 * Max 500 per request.
	 */
	suspend fun getAllPlatforms(): List<IgdbPlatformDto> {
		return runCatching {
			query<List<IgdbPlatformDto>>(
				endpoint = "platforms", body = """
					fields name, abbreviation, generation;
					where generation >= 7 | id = 6;
					sort generation desc;
					limit 30;
				""".trimIndent()
			)
		}.getOrElse {
			Log.e(TAG, "Error in recovering platforms: ${it.message}")
			emptyList()
		}
	}

	/**
	 * A generic internal helper to execute POST queries against the IGDB API endpoints.
	 *
	 * Automatically injects the required Twitch OAuth credentials ([clientId] and [accessToken])
	 * into the headers, configures the content type to [ContentType.Text.Plain] (as expected by
	 * the Apicalypse query language), and deserializes the response payload into the reified type [T].
	 *
	 * @param endpoint The target IGDB API endpoint (e.g., "games").
	 * @param body The Apicalypse query string containing fields, filters, and limits.
	 * @return The parsed response body of type [T].
	 */
	private suspend inline fun <reified T> query(
		endpoint: String, body: String
	): T {
		return httpClient.post("$baseUrl/$endpoint") {
			header("Client-ID", clientId)
			header("Authorization", "Bearer $accessToken")
			contentType(ContentType.Text.Plain)
			setBody(body)
		}.body()
	}
}