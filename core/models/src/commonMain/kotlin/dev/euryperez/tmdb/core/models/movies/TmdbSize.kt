package dev.euryperez.tmdb.core.models.movies

/**
 * Represents the dimensions of an image from The Movie Database (TMDb).
 *
 * This data class encapsulates the width and height of an image, providing better
 * type safety and semantic clarity compared to separate width and height properties.
 *
 * @property width The width of the image in pixels.
 * @property height The height of the image in pixels.
 */
data class TmdbSize(
    val width: Int,
    val height: Int,
) {
    companion object
}
