package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UpcomingMoviesResponseDTO(
    val page: Int,
    val results: List<MovieDTO>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int,
    val dates: DatesDTO,
) {
    companion object
}

@Serializable
internal data class DatesDTO(val maximum: String, val minimum: String) {
    companion object
}
