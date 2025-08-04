package dev.euryperez.tmdb.core.models.movies

import dev.euryperez.tmdb.core.models.constants.TmdbConstants

/**
 * Represents a movie image from The Movie Database (TMDb).
 *
 * This data class holds information about an individual movie image, including its
 * aspect ratio, file path, dimensions, language, and community ratings.
 *
 * @property aspectRatio The aspect ratio of the image (width/height).
 * @property filePath The relative file path of the image on TMDb servers.
 * @property size The dimensions of the image encapsulated in a TmdbSize object.
 * @property languageCode The language code for the image (e.g., "en", "es"), null for language-neutral images.
 * @property voteAverage The average community rating for this image.
 * @property voteCount The total number of votes this image has received.
 */
data class TmdbMovieImage(
    val aspectRatio: Double,
    val filePath: String,
    val size: TmdbSize,
    val languageCode: String?,
    val voteAverage: Double,
    val voteCount: Int,
) {
    /**
     * Returns the full URL for the image in its original size.
     *
     * @return The full URL string for the image.
     */
    val imageUrl: String = "${TmdbConstants.IMAGE_BASE_URL}original$filePath"

    companion object
}
