package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.models.movies.TmdbGenre
import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.models.movies.TmdbMovieDetails
import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.coroutines.TestDispatchers
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.core.utils.coroutines.DispatcherProvider
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.UpcomingMoviesResponseDTO
import dev.euryperez.tmdb.data.movies.mappers.toDomain
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesRepositoryTest : BaseTest {

    val mainCoroutineRule = MainCoroutineRule()

    private val moviesApi = mock<MoviesApi>()

    @BeforeTest
    override fun setup() {
        mainCoroutineRule.setup()
    }

    @AfterTest
    override fun tearDown() {
        mainCoroutineRule.tearDown()
    }

    @Test
    fun `getPopularMovies returns success with mapped movies when api call succeeds`() = runTest {
        // Given
        val page = 1
        val language = "en-US"
        val expectedMovies = listOf(
            TmdbMovie(
                id = 1,
                title = "Test Movie",
                overview = "Test overview",
                posterPath = "/test-poster.jpg",
                backdropPath = "/test-backdrop.jpg",
                releaseDate = LocalDate(2023, 5, 2),
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                genreIds = listOf(28, 12),
                adult = false,
                originalLanguage = "en",
                originalTitle = "Test Movie",
                video = false,
            ),
        )

        everySuspend {
            moviesApi.getPopularMovies(page = page, language = language)
        } returns ApiResult.Success(MovieListResponseDTO.test())

        // When
        val result = MoviesRepository.test().getPopularMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expectedMovies.size, result.data.size)
        assertEquals(expectedMovies, result.data)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getPopularMovies(page = page, language = language)
        }
    }

    @Test
    fun `getPopularMovies returns failure when api call fails`() = runTest {
        // Given
        val page = 1
        val language = "en-US"
        val errorMessage = "Network error"

        everySuspend {
            moviesApi.getPopularMovies(page = page, language = language)
        } returns ApiResult.Error.HttpError(401, errorMessage)

        // When
        val result = MoviesRepository.test().getPopularMovies(page, language)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getPopularMovies(page = page, language = language)
        }
    }

    @Test
    fun `getPopularMovies returns empty list when api returns empty results`() = runTest {
        // Given
        val page = 1
        val language = "en-US"

        everySuspend {
            moviesApi.getPopularMovies(page = page, language = language)
        } returns ApiResult.Success(MovieListResponseDTO.test(movies = emptyList()))

        // When
        val result = MoviesRepository.test().getPopularMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertTrue(result.data.isEmpty())

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getPopularMovies(page = page, language = language)
        }
    }

    @Test
    fun `getPopularMovies handles multiple movies correctly`() = runTest {
        // Given
        val page = 1
        val language = "en-US"
        val movieDtos = listOf(
            MovieDTO.test(id = 1, title = "Movie 1"),
            MovieDTO.test(id = 2, title = "Movie 2"),
            MovieDTO.test(id = 3, title = "Movie 3"),
        )

        val mockApiResponse = MovieListResponseDTO.test(movies = movieDtos)

        val expectedMovies = movieDtos.map { it.toDomain() }

        everySuspend {
            moviesApi.getPopularMovies(page = page, language = language)
        } returns ApiResult.Success(mockApiResponse)

        // When
        val repository = MoviesRepository.test()
        val result = repository.getPopularMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(3, result.data.size)
        assertEquals(expectedMovies, result.data)
    }

    @Test
    fun `getPopularMovies passes correct parameters to api`() = runTest {
        // Given
        val page = 2
        val language = "es-ES"

        everySuspend {
            moviesApi.getPopularMovies(page = page, language = language)
        } returns ApiResult.Success(MovieListResponseDTO.test(movies = emptyList()))

        // When
        MoviesRepository.test().getPopularMovies(page, language)

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getPopularMovies(page = 2, language = "es-ES")
        }
    }

    @Test
    fun `getUpcomingMovies returns success with mapped movies when api call succeeds`() = runTest {
        // Given
        val page = 1
        val language = "en-US"
        val expectedMovies = listOf(
            TmdbMovie(
                id = 1,
                title = "Test Movie",
                overview = "Test overview",
                posterPath = "/test-poster.jpg",
                backdropPath = "/test-backdrop.jpg",
                releaseDate = LocalDate(2023, 5, 2),
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                genreIds = listOf(28, 12),
                adult = false,
                originalLanguage = "en",
                originalTitle = "Test Movie",
                video = false,
            ),
        )

        everySuspend {
            moviesApi.getUpcomingMovies(page = page, language = language)
        } returns ApiResult.Success(UpcomingMoviesResponseDTO.test())

        // When
        val result = MoviesRepository.test().getUpcomingMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expectedMovies.size, result.data.size)
        assertEquals(expectedMovies, result.data)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getUpcomingMovies(page = page, language = language)
        }
    }

    @Test
    fun `getUpcomingMovies returns failure when api call fails`() = runTest {
        // Given
        val page = 1
        val language = "en-US"
        val errorMessage = "Network error"

        everySuspend {
            moviesApi.getUpcomingMovies(page = page, language = language)
        } returns ApiResult.Error.HttpError(401, errorMessage)

        // When
        val result = MoviesRepository.test().getUpcomingMovies(page, language)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getUpcomingMovies(page = page, language = language)
        }
    }

    @Test
    fun `getUpcomingMovies returns empty list when api returns empty results`() = runTest {
        // Given
        val page = 1
        val language = "en-US"

        everySuspend {
            moviesApi.getUpcomingMovies(page = page, language = language)
        } returns ApiResult.Success(UpcomingMoviesResponseDTO.test(movies = emptyList()))

        // When
        val result = MoviesRepository.test().getUpcomingMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertTrue(result.data.isEmpty())

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getUpcomingMovies(page = page, language = language)
        }
    }

    @Test
    fun `getUpcomingMovies handles multiple movies correctly`() = runTest {
        // Given
        val page = 1
        val language = "en-US"
        val movieDtos = listOf(
            MovieDTO.test(id = 1, title = "Upcoming Movie 1"),
            MovieDTO.test(id = 2, title = "Upcoming Movie 2"),
            MovieDTO.test(id = 3, title = "Upcoming Movie 3"),
        )

        everySuspend {
            moviesApi.getUpcomingMovies(page = page, language = language)
        } returns ApiResult.Success(UpcomingMoviesResponseDTO.test(movies = movieDtos))

        // When
        val result = MoviesRepository.test().getUpcomingMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(3, result.data.size)
        assertEquals("Upcoming Movie 1", result.data[0].title)
        assertEquals("Upcoming Movie 2", result.data[1].title)
        assertEquals("Upcoming Movie 3", result.data[2].title)
    }

    @Test
    fun `getUpcomingMovies uses default parameters when not specified`() = runTest {
        // Given
        everySuspend {
            moviesApi.getUpcomingMovies(page = 1, language = "en-US")
        } returns ApiResult.Success(UpcomingMoviesResponseDTO.test(movies = emptyList()))

        // When
        MoviesRepository.test().getUpcomingMovies()

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getUpcomingMovies(page = 1, language = "en-US")
        }
    }

    @Test
    fun `getUpcomingMovies passes correct parameters to api`() = runTest {
        // Given
        val page = 3
        val language = "fr-FR"

        everySuspend {
            moviesApi.getUpcomingMovies(page = page, language = language)
        } returns ApiResult.Success(UpcomingMoviesResponseDTO.test(movies = emptyList()))

        // When
        MoviesRepository.test().getUpcomingMovies(page, language)

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getUpcomingMovies(page = 3, language = "fr-FR")
        }
    }

    @Test
    fun `searchMovies returns success with mapped movies when api call succeeds`() = runTest {
        // Given
        val query = "test query"
        val page = 1
        val language = "en-US"
        val expectedMovies = listOf(
            TmdbMovie(
                id = 1,
                title = "Test Movie",
                overview = "Test overview",
                posterPath = "/test-poster.jpg",
                backdropPath = "/test-backdrop.jpg",
                releaseDate = LocalDate(2023, 5, 2),
                voteAverage = 8.5,
                voteCount = 1000,
                popularity = 100.0,
                genreIds = listOf(28, 12),
                adult = false,
                originalLanguage = "en",
                originalTitle = "Test Movie",
                video = false,
            ),
        )

        everySuspend {
            moviesApi.searchMovies(query = query, page = page, language = language)
        } returns ApiResult.Success(MovieListResponseDTO.test())

        // When
        val result = MoviesRepository.test().searchMovies(query, page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expectedMovies.size, result.data.size)
        assertEquals(expectedMovies, result.data)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.searchMovies(query = query, page = page, language = language)
        }
    }

    @Test
    fun `searchMovies returns failure when api call fails`() = runTest {
        // Given
        val query = "test query"
        val page = 1
        val language = "en-US"
        val errorMessage = "Network error"

        everySuspend {
            moviesApi.searchMovies(query, page = page, language = language)
        } returns ApiResult.Error.HttpError(401, errorMessage)

        // When
        val result = MoviesRepository.test().searchMovies(query, page, language)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.searchMovies(query = query, page = page, language = language)
        }
    }

    @Test
    fun `searchMovies returns empty list when api returns empty results`() = runTest {
        // Given
        val query = "test query"
        val page = 1
        val language = "en-US"

        everySuspend {
            moviesApi.searchMovies(query, page = page, language = language)
        } returns ApiResult.Success(MovieListResponseDTO.test(movies = emptyList()))

        // When
        val result = MoviesRepository.test().searchMovies(query, page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertTrue(result.data.isEmpty())

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.searchMovies(query = query, page = page, language = language)
        }
    }

    @Test
    fun `searchMovies handles multiple movies correctly`() = runTest {
        // Given
        val query = "test query"
        val page = 1
        val language = "en-US"
        val movieDtos = listOf(
            MovieDTO.test(id = 1, title = "Upcoming Movie 1"),
            MovieDTO.test(id = 2, title = "Upcoming Movie 2"),
            MovieDTO.test(id = 3, title = "Upcoming Movie 3"),
        )

        val expectedMovies = movieDtos.map { it.toDomain() }

        everySuspend {
            moviesApi.searchMovies(query, page = page, language = language)
        } returns ApiResult.Success(MovieListResponseDTO.test(movies = movieDtos))

        // When
        val result = MoviesRepository.test().searchMovies(query, page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(3, result.data.size)
        assertEquals(expectedMovies, result.data)

        verifySuspend {
            moviesApi.searchMovies(query = query, page = page, language = language)
        }
    }

    @Test
    fun `searchMovies uses default parameters when not specified`() = runTest {
        // Given
        everySuspend {
            moviesApi.searchMovies(query = "test query", page = 1, language = "en-US")
        } returns ApiResult.Success(MovieListResponseDTO.test(movies = emptyList()))

        // When
        MoviesRepository.test().searchMovies(query = "test query")

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.searchMovies(query = "test query", page = 1, language = "en-US")
        }
    }

    @Test
    fun `searchMovies passes correct parameters to api`() = runTest {
        // Given
        val query = "test query"
        val page = 3
        val language = "fr-FR"

        everySuspend {
            moviesApi.searchMovies(query = query, page = page, language = language)
        } returns ApiResult.Success(MovieListResponseDTO.test(movies = emptyList()))

        // When
        MoviesRepository.test().searchMovies(query, page, language)

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.searchMovies(query = query, page = 3, language = "fr-FR")
        }
    }

    @Test
    fun `getMovieDetails returns success with mapped movie details when api call succeeds`() = runTest {
        // Given
        val movieId = 42
        val language = "en-US"

        val expectedDetails = TmdbMovieDetails(
            id = 42,
            title = "The Answer",
            overview = "Life, the universe and everything.",
            posterPath = "/answer.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = LocalDate(1979, 3, 4),
            voteAverage = 9.9,
            voteCount = 420_000,
            popularity = 1000.0,
            genres = listOf(TmdbGenre(14, "Sci‑Fi")),
            adult = false,
            originalLanguage = "en",
            originalTitle = "The Answer",
            video = false,
            productionCompanies = emptyList(),
            productionCountries = emptyList(),
            revenue = 1337,
            runtime = 113,
            budget = 4242,
            homepage = "https://example.com/the-answer",
        )

        everySuspend {
            moviesApi.getMovieDetails(movieId = movieId, language = language)
        } returns ApiResult.Success(MovieDetailsDTO.test()) // or your own fixture builder

        // When
        val result = MoviesRepository.test()
            .getMovieDetails(movieId, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expectedDetails, result.data)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getMovieDetails(movieId = movieId, language = language)
        }
    }

    @Test
    fun `getMovieDetails returns failure when api call fails`() = runTest {
        // Given
        val movieId = 404
        val language = "en-US"

        everySuspend {
            moviesApi.getMovieDetails(movieId = movieId, language = language)
        } returns ApiResult.Error.HttpError(401, "test error")

        // When
        val result = MoviesRepository.test()
            .getMovieDetails(movieId, language)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals("test error", result.message)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getMovieDetails(movieId = movieId, language = language)
        }
    }

    @Test
    fun `getMovieCredits returns success with mapped credits when api call succeeds`() = runTest {
        val movieId = 42
        val language = "en-US"
        val dto = MovieCreditsResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Success(dto))

        val result = MoviesRepository.test().getMovieCredits(movieId, language)

        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieCredits(movieId, language)
        }
    }

    @Test
    fun `getMovieCredits maps correctly when cast and crew are empty`() = runTest {
        val movieId = 42
        val language = "en-US"
        val dtoEmpty = MovieCreditsResponseDTO.test().copy(
            cast = emptyList(),
            crew = emptyList(),
        )

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Success(dtoEmpty))

        val result = MoviesRepository.test().getMovieCredits(movieId, language)

        result as DataResult.Success
        assertTrue(result.data.cast.isEmpty())
        assertTrue(result.data.crew.isEmpty())
    }

    @Test
    fun `getMovieCredits returns failure with message when http error occurs`() = runTest {
        val movieId = 42
        val language = "en-US"
        val errorMsg = "Not authorised"

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Error.HttpError(401, errorMsg))

        val result = MoviesRepository.test().getMovieCredits(movieId, language)

        assertTrue(result is DataResult.Failure)
        assertEquals(errorMsg, result.message)
    }

    @Test
    fun `getMovieCredits returns failure with message when network error occurs`() = runTest {
        val movieId = 42
        val language = "en-US"
        val errorMsg = "Connection lost"

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Error.NetworkError(errorMsg))

        val result = MoviesRepository.test().getMovieCredits(movieId, language)

        assertTrue(result is DataResult.Failure)
        assertEquals(errorMsg, result.message)
    }

    @Test
    fun `getMovieCredits returns failure with message when serialization error occurs`() = runTest {
        val movieId = 42
        val language = "en-US"
        val errorMsg = "JSON mismatch"

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Error.SerializationError(errorMsg))

        val result = MoviesRepository.test().getMovieCredits(movieId, language)

        assertTrue(result is DataResult.Failure)
        assertEquals(errorMsg, result.message)
    }

    @Test
    fun `getMovieCredits forwards the provided language to the api`() = runTest {
        val movieId = 42
        val language = "es-ES"

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Success(MovieCreditsResponseDTO.test()))

        MoviesRepository.test().getMovieCredits(movieId, language)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieCredits(movieId, language)
        }
    }

    private fun MoviesRepository.Companion.test(
        moviesApi: MoviesApi = this@MoviesRepositoryTest.moviesApi,
        dispatcherProvider: DispatcherProvider = TestDispatchers(),
    ) = MoviesRepositoryImpl(
        moviesApi = moviesApi,
        dispatcherProvider = dispatcherProvider,
    )
}
