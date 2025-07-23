package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieListResponseDTO(
    val page: Int,
    val results: List<MovieDTO>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int,
) {
    companion object
}
