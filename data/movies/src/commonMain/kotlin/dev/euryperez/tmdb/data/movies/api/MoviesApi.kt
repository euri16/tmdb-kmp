package dev.euryperez.tmdb.data.movies.api

import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.UpcomingMoviesResponseDTO
import dev.euryperez.tmdb.core.network.buildHttpClient
import dev.euryperez.tmdb.core.network.models.ApiResult

internal interface MoviesApi {
    suspend fun getPopularMovies(
        page: Int = 1,
        language: String = "en-US"
    ): ApiResult<MovieListResponseDTO>

    suspend fun getUpcomingMovies(
        page: Int = 1,
        language: String = "en-US"
    ): ApiResult<UpcomingMoviesResponseDTO>

    suspend fun getMovieDetails(
        movieId: Int,
        language: String = "en-US"
    ): ApiResult<MovieDetailsDTO>

    // TODO: Move to the :data:search module
    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        language: String = "en-US",
        year: Int? = null
    ): ApiResult<MovieListResponseDTO>

    suspend fun getMovieCredits(
        movieId: Int,
        language: String = "en-US"
    ): ApiResult<MovieCreditsResponseDTO>

    companion object {
        private var instance: MoviesApi? = null

        fun create(apiKey: String): MoviesApi {
            return instance ?: MoviesApiImpl(httpClient = buildHttpClient(apiKey = apiKey))
                .also { instance = it }
        }
    }
}