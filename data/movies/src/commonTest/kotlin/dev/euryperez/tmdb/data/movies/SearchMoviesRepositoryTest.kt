package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.mappers.toDomain
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
class SearchMoviesRepositoryTest : BaseTest {

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
        val result = MoviesRepository.test(moviesApi).searchMovies(query, page, language)

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
        val result = MoviesRepository.test(moviesApi).searchMovies(query, page, language)

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
        val result = MoviesRepository.test(moviesApi).searchMovies(query, page, language)

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
        val result = MoviesRepository.test(moviesApi).searchMovies(query, page, language)

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
        MoviesRepository.test(moviesApi).searchMovies(query = "test query")

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
        MoviesRepository.test(moviesApi).searchMovies(query, page, language)

        // Then
        verifySuspend(mode = VerifyMode.exactly(1)) {
            moviesApi.searchMovies(query = query, page = 3, language = "fr-FR")
        }
    }
}
