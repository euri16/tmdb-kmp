package dev.euryperez.tmdb.data.movies.api

import dev.euryperez.tmdb.core.network.buildHttpClient
import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.data.movies.api.dtos.MovieAlternativeTitlesResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieExternalIdsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.NowPlayingMoviesResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.UpcomingMoviesResponseDTO

internal interface MoviesApi {
    suspend fun getPopularMovies(page: Int = 1, language: String = "en-US"): ApiResult<MovieListResponseDTO>

    suspend fun getUpcomingMovies(page: Int = 1, language: String = "en-US"): ApiResult<UpcomingMoviesResponseDTO>

    suspend fun getNowPlayingMovies(page: Int = 1, language: String = "en-US"): ApiResult<NowPlayingMoviesResponseDTO>

    suspend fun getTopRatedMovies(page: Int = 1, language: String = "en-US"): ApiResult<MovieListResponseDTO>

    suspend fun getMovieDetails(movieId: Int, language: String = "en-US"): ApiResult<MovieDetailsDTO>

    // TODO: Move to the :data:search module
    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        language: String = "en-US",
        year: Int? = null,
    ): ApiResult<MovieListResponseDTO>

    suspend fun getMovieCredits(movieId: Int, language: String = "en-US"): ApiResult<MovieCreditsResponseDTO>

    suspend fun getMovieAlternativeTitles(movieId: Int): ApiResult<MovieAlternativeTitlesResponseDTO>

    suspend fun getMovieExternalIds(movieId: Int): ApiResult<MovieExternalIdsResponseDTO>

    companion object {
        private var instance: MoviesApi? = null

        fun getInstance(apiKey: String): MoviesApi {
            return instance ?: MoviesApiImpl(httpClient = buildHttpClient(apiKey = apiKey))
                .also { instance = it }
        }

        fun factory(apiKey: String): MoviesApi {
            return MoviesApiImpl(httpClient = buildHttpClient(apiKey = apiKey))
        }
    }
}
