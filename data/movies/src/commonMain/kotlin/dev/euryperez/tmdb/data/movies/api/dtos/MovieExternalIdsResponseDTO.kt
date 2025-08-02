package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieExternalIdsResponseDTO(
    val id: Int,
    @SerialName("imdb_id")
    val imdbId: String?,
    @SerialName("wikidata_id")
    val wikidataId: String?,
    @SerialName("facebook_id")
    val facebookId: String?,
    @SerialName("instagram_id")
    val instagramId: String?,
    @SerialName("twitter_id")
    val twitterId: String?,
)
