package dev.euryperez.tmdb.data.movies.api

import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.UpcomingMoviesResponseDTO
import dev.euryperez.tmdb.data.movies.api.resources.MovieResource
import dev.euryperez.tmdb.data.movies.api.resources.SearchResource
import dev.euryperez.tmdb.core.network.extensions.safeCall
import dev.euryperez.tmdb.core.network.models.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get

internal class MoviesApiImpl(val httpClient: HttpClient) : MoviesApi {
    override suspend fun getPopularMovies(
        page: Int,
        language: String
    ): ApiResult<MovieListResponseDTO> {
        return httpClient.safeCall {
            get(MovieResource.Popular(page = page, language = language))
        }
    }

    override suspend fun getUpcomingMovies(
        page: Int,
        language: String
    ): ApiResult<UpcomingMoviesResponseDTO> {
        return httpClient.safeCall {
            get(MovieResource.Upcoming(page = page, language = language)).body()
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String
    ): ApiResult<MovieDetailsDTO> {
        return httpClient.safeCall {
            get(MovieResource.Id(movieId = movieId, language = language)).body()
        }
    }

    // TODO: Move to the :data:search module
    override suspend fun searchMovies(
        query: String,
        page: Int,
        language: String,
        year: Int?
    ): ApiResult<MovieListResponseDTO> {
        return httpClient.safeCall {
            get(
                SearchResource.MovieResource(
                    query = query,
                    page = page,
                    language = language,
                    year = year
                )
            ).body()
        }
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String
    ): ApiResult<MovieCreditsResponseDTO> {
        return httpClient.safeCall {
            get(
                MovieResource.Id.Credits(
                    parent = MovieResource.Id(movieId = movieId, language = language),
                    language = language
                )
            ).body()
        }
    }

    companion object
}