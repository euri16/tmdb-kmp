package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieCreditsResponseDTO(
    val id: Int,
    val cast: List<MovieMemberDTO>,
    val crew: List<MovieMemberDTO>
)

@Serializable
internal data class MovieMemberDTO(
    val adult: Boolean,
    val gender: Int?, // 0 = not specified, 1 = female, 2 = male, 3 = non-binary
    val id: Int,
    @SerialName("known_for_department")
    val knownForDepartment: String,
    val name: String,
    @SerialName("original_name")
    val originalName: String,
    val popularity: Double,
    @SerialName("profile_path")
    val profilePath: String?,
    @SerialName("cast_id")
    val castId: Int?,
    val character: String?,
    @SerialName("credit_id")
    val creditId: String,
    val order: Int?,
    val department: String?,
    val job: String?
)
