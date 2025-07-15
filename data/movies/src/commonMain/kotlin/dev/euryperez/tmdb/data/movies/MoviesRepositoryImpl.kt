package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.models.movies.TmdbMovieCredits
import dev.euryperez.tmdb.core.models.movies.TmdbMovieDetails
import dev.euryperez.tmdb.core.utils.coroutines.DispatcherProvider
import dev.euryperez.tmdb.core.utils.coroutines.DispatcherProviderImpl
import dev.euryperez.tmdb.data.common.extensions.toDataResult
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.mappers.toDomain
import kotlinx.coroutines.withContext

internal class MoviesRepositoryImpl(
    private val moviesApi: MoviesApi,
    private val dispatcherProvider: DispatcherProvider = DispatcherProviderImpl()
) : MoviesRepository {
    override suspend fun getPopularMovies(
        page: Int,
        language: String
    ): DataResult<List<TmdbMovie>> {
        return withContext(dispatcherProvider.default) {
            moviesApi.getPopularMovies(page = page, language = language)
                .toDataResult()
                .map { it.results.map { dto -> dto.toDomain() } }
        }
    }

    override suspend fun getUpcomingMovies(
        page: Int,
        language: String
    ): DataResult<List<TmdbMovie>> {
        return withContext(dispatcherProvider.default) {
            moviesApi.getUpcomingMovies(page = page, language = language)
                .toDataResult()
                .map { it.results.map { dto -> dto.toDomain() } }
        }
    }

    override suspend fun getMovieDetails(
        movieId: Int,
        language: String
    ): DataResult<TmdbMovieDetails> {
        return withContext(dispatcherProvider.default) {
            moviesApi.getMovieDetails(movieId = movieId, language = language)
                .toDataResult()
                .map { it.toDomain() }
        }
    }

    override suspend fun searchMovies(
        query: String,
        page: Int,
        language: String,
        year: Int?
    ): DataResult<List<TmdbMovie>> {
        return withContext(dispatcherProvider.default) {
            moviesApi.searchMovies(
                query = query,
                page = page,
                language = language,
                year = year
            ).toDataResult().map { it.results.map { dto -> dto.toDomain() } }
        }
    }

    override suspend fun getMovieCredits(
        movieId: Int,
        language: String
    ): DataResult<TmdbMovieCredits> {
        return withContext(dispatcherProvider.default) {
            moviesApi.getMovieCredits(movieId = movieId, language = language)
                .toDataResult()
                .map { it.toDomain() }
        }
    }
}
