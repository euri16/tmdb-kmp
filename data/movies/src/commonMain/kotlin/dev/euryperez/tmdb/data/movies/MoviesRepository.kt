package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.models.movies.TmdbMovieAlternativeTitles
import dev.euryperez.tmdb.core.models.movies.TmdbMovieCredits
import dev.euryperez.tmdb.core.models.movies.TmdbMovieDetails
import dev.euryperez.tmdb.core.models.movies.TmdbMovieExternalIds
import dev.euryperez.tmdb.core.models.movies.TmdbMovieImages
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi

interface MoviesRepository {
    suspend fun getPopularMovies(page: Int = 1, language: String = "en-US"): DataResult<List<TmdbMovie>>

    suspend fun getUpcomingMovies(page: Int = 1, language: String = "en-US"): DataResult<List<TmdbMovie>>

    suspend fun getNowPlayingMovies(page: Int = 1, language: String = "en-US"): DataResult<List<TmdbMovie>>

    suspend fun getTopRatedMovies(page: Int = 1, language: String = "en-US"): DataResult<List<TmdbMovie>>

    suspend fun getMovieDetails(movieId: Int, language: String = "en-US"): DataResult<TmdbMovieDetails>

    // TODO: Move to the :data:search module
    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        language: String = "en-US",
        year: Int? = null,
    ): DataResult<List<TmdbMovie>>

    suspend fun getMovieCredits(movieId: Int, language: String = "en-US"): DataResult<TmdbMovieCredits>

    suspend fun getMovieAlternativeTitles(movieId: Int): DataResult<TmdbMovieAlternativeTitles>

    suspend fun getMovieExternalIds(movieId: Int): DataResult<TmdbMovieExternalIds>

    suspend fun getMovieImages(
        movieId: Int,
        language: String? = null,
        includeImageLanguage: List<String>? = null,
    ): DataResult<TmdbMovieImages>

    companion object {
        private var instance: MoviesRepository? = null

        fun getInstance(apiKey: String): MoviesRepository {
            return instance ?: factory(apiKey).also { instance = it }
        }

        fun factory(apiKey: String): MoviesRepository {
            return MoviesRepositoryImpl(moviesApi = MoviesApi.factory(apiKey))
        }
    }
}
