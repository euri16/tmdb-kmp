package dev.euryperez.tmdb.data.movies.api

import dev.euryperez.tmdb.core.network.extensions.getAsApiResult
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.UpcomingMoviesResponseDTO
import dev.euryperez.tmdb.data.movies.api.resources.MovieResource
import dev.euryperez.tmdb.data.movies.api.resources.SearchResource
import dev.euryperez.tmdb.core.network.models.ApiResult
import io.ktor.client.HttpClient

internal class MoviesApiImpl(val httpClient: HttpClient) : MoviesApi {
    override suspend fun getPopularMovies(
        page: Int,
        language: String
    ): ApiResult<MovieListResponseDTO> {
        return httpClient.getAsApiResult(
            MovieResource.Popular(page = page, language = language)
        )
    }

    override suspend fun getUpcomingMovies(
        page: Int,
        language: String
    ): ApiResult<UpcomingMoviesResponseDTO> {
        return httpClient.getAsApiResult(
            MovieResource.Upcoming(page = page, language = language)
        )
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String
    ): ApiResult<MovieDetailsDTO> {
        return httpClient.getAsApiResult(
            MovieResource.Id(movieId = movieId, language = language)
        )
    }

    // TODO: Move to the :data:search module
    override suspend fun searchMovies(
        query: String,
        page: Int,
        language: String,
        year: Int?
    ): ApiResult<MovieListResponseDTO> {
        return httpClient.getAsApiResult(
            SearchResource.MovieResource(
                query = query,
                page = page,
                language = language,
                year = year
            )
        )
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String
    ): ApiResult<MovieCreditsResponseDTO> {
        return httpClient.getAsApiResult(
            MovieResource.Id.Credits(
                parent = MovieResource.Id(movieId = movieId, language = language),
                language = language
            )
        )
    }

    companion object
}