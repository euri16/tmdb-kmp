package dev.euryperez.tmdb.core.models.movies

import dev.euryperez.tmdb.core.models.constants.TmdbConstants
import dev.euryperez.tmdb.core.models.sizes.PosterSize
import kotlinx.datetime.LocalDate

/**
 * Represents a movie from The Movie Database (TMDb).
 *
 * This data class holds information about a movie, including its ID, title, overview,
 * poster and backdrop paths, release date, ratings, popularity, genre IDs, and other details.
 *
 * @property id The unique identifier of the movie.
 * @property title The title of the movie.
 * @property overview A brief summary of the movie's plot.
 * @property posterUrl The full URL for the movie's poster image in its original size.
 * @property backdropUrl The full URL for the movie's backdrop image in its original size.
 * @property releaseDate The release date of the movie. Can be null if the release date is unknown.
 * @property voteAverage The average vote score for the movie (typically on a scale of 0-10).
 * @property voteCount The total number of votes the movie has received.
 * @property popularity A numerical value representing the movie's popularity.
 * @property genreIds A list of integer IDs representing the genres of the movie.
 * @property adult A boolean indicating whether the movie is classified as adult content.
 * @property originalLanguage The original language of the movie (e.g., "en" for English).
 * @property originalTitle The original title of the movie, which might differ from the
 *                         localized title.
 * @property video A boolean indicating whether the movie has an associated video (e.g., a trailer).
 */
data class TmdbMovie(
    val id: Int,
    val title: String,
    val overview: String,
    private val posterPath: String?,
    private val backdropPath: String?,
    val releaseDate: LocalDate?,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val genreIds: List<Int>, // TODO: Return the genre names instead of IDs
    val adult: Boolean,
    val originalLanguage: String,
    val originalTitle: String,
    val video: Boolean,
) {
    val posterUrl: String? = posterPath?.let { "${TmdbConstants.IMAGE_BASE_URL}original$it" }

    val backdropUrl: String? = backdropPath?.let { "${TmdbConstants.IMAGE_BASE_URL}original$it" }

    /**
     * Returns the full URL for the movie's poster image.
     *
     * @param size The desired size of the poster image.
     * @return The full URL string for the poster image, or null 
     *         if the poster path is not available.
     */
    fun getPosterUrl(size: PosterSize): String? {
        return posterPath?.let { "${TmdbConstants.IMAGE_BASE_URL}${size.value}$it" }
    }

    /**
     * Returns the full URL for the movie's backdrop image.
     *
     * @param size The desired size of the backdrop image.
     * @return The full URL string for the backdrop image, or null 
     *         if the backdrop path is not available.
     */
    fun getBackdropUrl(size: PosterSize): String? {
        return backdropPath?.let { "${TmdbConstants.IMAGE_BASE_URL}${size.value}$it" }
    }

    companion object
}