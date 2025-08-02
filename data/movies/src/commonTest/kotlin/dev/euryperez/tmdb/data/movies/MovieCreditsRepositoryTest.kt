package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
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
class MovieCreditsRepositoryTest : BaseTest {

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
    fun `getMovieCredits returns success with mapped credits when api call succeeds`() = runTest {
        val movieId = 42
        val language = "en-US"
        val dto = MovieCreditsResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Success(dto))

        val result = MoviesRepository.test(moviesApi).getMovieCredits(movieId, language)

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

        val result = MoviesRepository.test(moviesApi).getMovieCredits(movieId, language)

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

        val result = MoviesRepository.test(moviesApi).getMovieCredits(movieId, language)

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

        val result = MoviesRepository.test(moviesApi).getMovieCredits(movieId, language)

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

        val result = MoviesRepository.test(moviesApi).getMovieCredits(movieId, language)

        assertTrue(result is DataResult.Failure)
        assertEquals(errorMsg, result.message)
    }

    @Test
    fun `getMovieCredits forwards the provided language to the api`() = runTest {
        val movieId = 42
        val language = "es-ES"

        everySuspend { moviesApi.getMovieCredits(movieId, language) }
            .returns(ApiResult.Success(MovieCreditsResponseDTO.test()))

        MoviesRepository.test(moviesApi).getMovieCredits(movieId, language)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieCredits(movieId, language)
        }
    }
}
