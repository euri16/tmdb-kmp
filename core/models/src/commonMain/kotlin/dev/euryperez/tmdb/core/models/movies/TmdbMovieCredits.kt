package dev.euryperez.tmdb.core.models.movies

import dev.euryperez.tmdb.core.models.common.Gender
import dev.euryperez.tmdb.core.models.constants.TmdbConstants
import dev.euryperez.tmdb.core.models.sizes.ProfileSize

data class TmdbMovieCredits(
    val id: Int,
    val cast: List<TmdbMember>,
    val crew: List<TmdbMember>,
) {
    companion object
}

data class TmdbMember(
    val id: Int,
    val name: String,
    val adult: Boolean,
    val gender: Gender, // 0 = not specified, 1 = female, 2 = male, 3 = non-binary
    val knownForDepartment: String,
    val originalName: String,
    val popularity: Double,
    private val profilePath: String?,
    val castId: Int?,
    val character: String?,
    val creditId: String,
    val order: Int?,
    val department: String?,
    val job: String?,
) {
    val profilePictureUrl: String?
        get() = profilePath?.let { "${TmdbConstants.IMAGE_BASE_URL}original$it" }

    fun getProfilePictureUrl(size: ProfileSize): String? {
        return profilePath?.let { "${TmdbConstants.IMAGE_BASE_URL}${size.value}$it" }
    }

    companion object
}
