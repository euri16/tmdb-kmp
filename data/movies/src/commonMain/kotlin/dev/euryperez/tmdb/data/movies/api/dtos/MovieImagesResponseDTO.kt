package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.Serializable

@Serializable
internal data class MovieImagesResponseDTO(
    val id: Int,
    val backdrops: List<MovieImageDTO>,
    val posters: List<MovieImageDTO>,
    val logos: List<MovieImageDTO>,
) {
    companion object
}
