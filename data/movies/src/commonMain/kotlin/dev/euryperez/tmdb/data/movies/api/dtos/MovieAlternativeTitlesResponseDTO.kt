package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieAlternativeTitlesResponseDTO(
    val id: Int,
    val titles: List<AlternativeTitleDTO>,
)

@Serializable
internal data class AlternativeTitleDTO(
    @SerialName("iso_3166_1")
    val iso31661: String,
    val title: String,
    val type: String? = null,
)
