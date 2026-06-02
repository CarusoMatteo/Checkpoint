package com.example.checkpoint.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Main DTO for a game returned by the IGDB API.
 * The fields are optional because IGDB only returns explicitly requested fields.
 */
@Serializable
data class IgdbGameDto(
	val id: Int,
	val name: String? = null,
	val summary: String? = null,
	val cover: IgdbCoverDto? = null,
	val genres: List<IgdbGenreDto>? = null,
	val platforms: List<IgdbPlatformDto>? = null,
	val collection: Int? = null,

	@SerialName("first_release_date") val firstReleaseDate: Long? = null,   // Unix timestamp

	@SerialName("involved_companies") val involvedCompanies: List<IgdbInvolvedCompanyDto>? = null,

	@SerialName("total_rating") val totalRating: Double? = null,

	@SerialName("total_rating_count") val totalRatingCount: Int? = null,

	val rating: Double? = null,

	@SerialName("aggregated_rating") val aggregatedRating: Double? = null,

	val storyline: String? = null,

	@SerialName("similar_games") val similarGames: List<Int>? = null,   // Only IDs
)

@Serializable
data class IgdbCoverDto(
	val id: Int, @SerialName("image_id") val imageId: String? = null
)

@Serializable
data class IgdbGenreDto(
	val id: Int, val name: String? = null
)

@Serializable
data class IgdbPlatformDto(
	val id: Int, val name: String? = null, val abbreviation: String? = null,

	@SerialName("platform_logo") val platformLogo: IgdbPlatformLogoDto? = null
)

@Serializable
data class IgdbPlatformLogoDto(
	val id: Int, @SerialName("image_id") val imageId: String? = null
)

@Serializable
data class IgdbInvolvedCompanyDto(
	val id: Int,
	val company: IgdbCompanyDto? = null,
	val developer: Boolean? = null,
	val publisher: Boolean? = null
)

@Serializable
data class IgdbCompanyDto(
	val id: Int, val name: String? = null
)

/**
 * DTO for search results (same structure as IgdbGameDto but
 * typically with fewer populated fields).
 */
typealias IgdbSearchResultDto = IgdbGameDto