package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.NowPlayingMoviesResponseDTO
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
class NowPlayingMoviesRepositoryTest : BaseTest {

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
    fun `getNowPlayingMovies returns success with mapped movies when api call succeeds`() = runTest {
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
            moviesApi.getNowPlayingMovies(page = page, language = language)
        } returns ApiResult.Success(NowPlayingMoviesResponseDTO.test())

        // When
        val result = MoviesRepository.test(moviesApi).getNowPlayingMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expectedMovies.size, result.data.size)
        assertEquals(expectedMovies, result.data)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getNowPlayingMovies(page = page, language = language)
        }
    }

    @Test
    fun `getNowPlayingMovies returns failure when api call fails`() = runTest {
        // Given
        val page = 1
        val language = "en-US"
        val errorMessage = "Network error"

        everySuspend {
            moviesApi.getNowPlayingMovies(page = page, language = language)
        } returns ApiResult.Error.HttpError(401, errorMessage)

        // When
        val result = MoviesRepository.test(moviesApi).getNowPlayingMovies(page, language)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getNowPlayingMovies(page = page, language = language)
        }
    }

    @Test
    fun `getNowPlayingMovies returns empty list when api returns empty results`() = runTest {
        // Given
        val page = 1
        val language = "en-US"

        everySuspend {
            moviesApi.getNowPlayingMovies(page = page, language = language)
        } returns ApiResult.Success(NowPlayingMoviesResponseDTO.test(movies = emptyList()))

        // When
        val result = MoviesRepository.test(moviesApi).getNowPlayingMovies(page, language)

        // Then
        assertTrue(result is DataResult.Success)
        assertTrue(result.data.isEmpty())

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getNowPlayingMovies(page = page, language = language)
        }
    }

    @Test
    fun `getNowPlayingMovies passes correct parameters to api`() = runTest {
        // Given
        val page = 2
        val language = "fr-FR"

        everySuspend {
            moviesApi.getNowPlayingMovies(page = page, language = language)
        } returns ApiResult.Success(NowPlayingMoviesResponseDTO.test(movies = emptyList()))

        // When
        MoviesRepository.test(moviesApi).getNowPlayingMovies(page, language)

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getNowPlayingMovies(page = 2, language = "fr-FR")
        }
    }
}
