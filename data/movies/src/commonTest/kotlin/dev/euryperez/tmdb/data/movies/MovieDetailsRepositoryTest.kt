package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.models.movies.TmdbGenre
import dev.euryperez.tmdb.core.models.movies.TmdbMovieDetails
import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
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
class MovieDetailsRepositoryTest : BaseTest {

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
        } returns ApiResult.Success(MovieDetailsDTO.test())

        // When
        val result = MoviesRepository.test(moviesApi)
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
        val result = MoviesRepository.test(moviesApi)
            .getMovieDetails(movieId, language)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals("test error", result.message)

        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.getMovieDetails(movieId = movieId, language = language)
        }
    }
}
