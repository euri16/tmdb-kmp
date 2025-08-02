package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.api.dtos.UpcomingMoviesResponseDTO
import dev.euryperez.tmdb.data.movies.utils.test
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
class UpcomingMoviesRepositoryTest : BaseTest {

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
        val result = MoviesRepository.test(moviesApi).getUpcomingMovies(page, language)

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
        val result = MoviesRepository.test(moviesApi).getUpcomingMovies(page, language)

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
        val result = MoviesRepository.test(moviesApi).getUpcomingMovies(page, language)

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
        val result = MoviesRepository.test(moviesApi).getUpcomingMovies(page, language)

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
        MoviesRepository.test(moviesApi).getUpcomingMovies()

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
        MoviesRepository.test(moviesApi).getUpcomingMovies(page, language)

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getUpcomingMovies(page = 3, language = "fr-FR")
        }
    }
}
