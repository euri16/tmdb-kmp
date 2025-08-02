package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieAlternativeTitlesResponseDTO
import dev.euryperez.tmdb.data.movies.mappers.toDomain
import dev.euryperez.tmdb.data.movies.utils.test
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieAlternativeTitlesRepositoryTest : BaseTest {

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
    fun `getMovieAlternativeTitles returns success with mapped alternative titles when api call succeeds`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieAlternativeTitlesResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(movieId)
        }
    }

    @Test
    fun `getMovieAlternativeTitles returns success with empty titles when api returns empty titles`() = runTest {
        // Given
        val movieId = 999
        val dto = MovieAlternativeTitlesResponseDTO.test(id = movieId, titles = emptyList())

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertTrue(result.data.titles.isEmpty())
        assertEquals(999, result.data.id)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(movieId)
        }
    }

    @Test
    fun `getMovieAlternativeTitles returns failure when api call fails with http error`() = runTest {
        // Given
        val movieId = 550
        val errorMessage = "Movie not found"

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Error.HttpError(404, errorMessage))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(movieId)
        }
    }

    @Test
    fun `getMovieAlternativeTitles returns failure when api call fails with network error`() = runTest {
        // Given
        val movieId = 550
        val errorMessage = "Network connection failed"

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Error.NetworkError(errorMessage))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(movieId)
        }
    }

    @Test
    fun `getMovieAlternativeTitles returns failure when api call fails with serialization error`() = runTest {
        // Given
        val movieId = 550
        val errorMessage = "JSON parsing error"

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Error.SerializationError(errorMessage))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(movieId)
        }
    }

    @Test
    fun `getMovieAlternativeTitles handles multiple alternative titles correctly`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieAlternativeTitlesResponseDTO.test(
            titles = listOf(
                dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO.test(
                    iso31661 = "US",
                    title = "Fight Club",
                    type = "Original Title",
                ),
                dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO.test(
                    iso31661 = "ES",
                    title = "El club de la lucha",
                    type = null,
                ),
                dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO.test(
                    iso31661 = "FR",
                    title = "Fight Club",
                    type = null,
                ),
            ),
        )

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(3, result.data.titles.size)
        assertEquals("Fight Club", result.data.titles[0].title)
        assertEquals("US", result.data.titles[0].countryCode)
        assertEquals("Original Title", result.data.titles[0].type)
        assertEquals("El club de la lucha", result.data.titles[1].title)
        assertEquals("ES", result.data.titles[1].countryCode)
        assertEquals(null, result.data.titles[1].type)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(movieId)
        }
    }

    @Test
    fun `getMovieAlternativeTitles passes correct movieId parameter to api`() = runTest {
        // Given
        val movieId = 12345
        val dto = MovieAlternativeTitlesResponseDTO.test()

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(12345)
        }
    }

    @Test
    fun `getMovieAlternativeTitles maps DTO to domain model correctly`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieAlternativeTitlesResponseDTO.test(
            id = 550,
            titles = listOf(
                dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO.test(
                    iso31661 = "DE",
                    title = "Fight Club",
                    type = "Theatrical Title",
                ),
            ),
        )

        everySuspend { moviesApi.getMovieAlternativeTitles(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieAlternativeTitles(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(550, result.data.id)
        assertEquals(1, result.data.titles.size)

        val title = result.data.titles[0]
        assertEquals("DE", title.countryCode)
        assertEquals("Fight Club", title.title)
        assertEquals("Theatrical Title", title.type)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieAlternativeTitles(movieId)
        }
    }
}
