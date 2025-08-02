package dev.euryperez.tmdb.data.movies.utils

import dev.euryperez.tmdb.core.test.coroutines.TestDispatchers
import dev.euryperez.tmdb.core.utils.coroutines.DispatcherProvider
import dev.euryperez.tmdb.data.movies.MoviesRepository
import dev.euryperez.tmdb.data.movies.MoviesRepositoryImpl
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
internal fun MoviesRepository.Companion.test(
    moviesApi: MoviesApi = mock<MoviesApi>(),
    dispatcherProvider: DispatcherProvider = TestDispatchers(),
) = MoviesRepositoryImpl(
    moviesApi = moviesApi,
    dispatcherProvider = dispatcherProvider,
)
