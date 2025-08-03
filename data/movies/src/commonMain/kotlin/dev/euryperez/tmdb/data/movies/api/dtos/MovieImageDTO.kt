package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieImageDTO(
    @SerialName("aspect_ratio")
    val aspectRatio: Double,
    @SerialName("file_path")
    val filePath: String,
    val height: Int,
    val width: Int,
    @SerialName("iso_639_1")
    val iso6391: String?,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    val voteCount: Int,
) {
    companion object
}
