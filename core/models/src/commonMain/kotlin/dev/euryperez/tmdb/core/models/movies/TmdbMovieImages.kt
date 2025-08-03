package dev.euryperez.tmdb.core.models.movies

/**
 * Represents the collection of images for a movie from The Movie Database (TMDb).
 *
 * This data class contains arrays of different types of movie images including
 * backdrops, posters, and logos, providing comprehensive image collections that
 * library consumers can use to display rich visual content.
 *
 * @property id The unique identifier of the movie.
 * @property backdrops A list of backdrop images (wide landscape images typically used as backgrounds).
 * @property posters A list of poster images (portrait movie posters in various sizes).
 * @property logos A list of logo images (movie title logos and branding assets).
 */
data class TmdbMovieImages(
    val id: Int,
    val backdrops: List<TmdbMovieImage>,
    val posters: List<TmdbMovieImage>,
    val logos: List<TmdbMovieImage>,
) {
    companion object
}
