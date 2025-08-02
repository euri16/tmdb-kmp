package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieExternalIdsResponseDTO
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieExternalIdsRepositoryTest : BaseTest {

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
    fun `getMovieExternalIds returns success with mapped external ids when api call succeeds`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieExternalIdsResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }

    @Test
    fun `getMovieExternalIds returns success with null external ids when api returns null values`() = runTest {
        // Given
        val movieId = 999
        val dto = MovieExternalIdsResponseDTO.test(
            id = movieId,
            imdbId = null,
            wikidataId = null,
            facebookId = null,
            instagramId = null,
            twitterId = null,
        )

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(999, result.data.id)
        assertNull(result.data.imdbId)
        assertNull(result.data.wikidataId)
        assertNull(result.data.facebookId)
        assertNull(result.data.instagramId)
        assertNull(result.data.twitterId)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }

    @Test
    fun `getMovieExternalIds returns failure when api call fails with http error`() = runTest {
        // Given
        val movieId = 550
        val errorMessage = "Movie not found"

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Error.HttpError(404, errorMessage))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }

    @Test
    fun `getMovieExternalIds returns failure when api call fails with network error`() = runTest {
        // Given
        val movieId = 550
        val errorMessage = "Network connection failed"

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Error.NetworkError(errorMessage))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }

    @Test
    fun `getMovieExternalIds returns failure when api call fails with serialization error`() = runTest {
        // Given
        val movieId = 550
        val errorMessage = "JSON parsing error"

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Error.SerializationError(errorMessage))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }

    @Test
    fun `getMovieExternalIds handles all external id types correctly`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieExternalIdsResponseDTO.test(
            id = movieId,
            imdbId = "tt0137523",
            wikidataId = "Q190050",
            facebookId = "FightClubFilm",
            instagramId = "fightclubmovie",
            twitterId = "fightclub",
        )

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(550, result.data.id)
        assertEquals("tt0137523", result.data.imdbId)
        assertEquals("Q190050", result.data.wikidataId)
        assertEquals("FightClubFilm", result.data.facebookId)
        assertEquals("fightclubmovie", result.data.instagramId)
        assertEquals("fightclub", result.data.twitterId)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }

    @Test
    fun `getMovieExternalIds passes correct movieId parameter to api`() = runTest {
        // Given
        val movieId = 12345
        val dto = MovieExternalIdsResponseDTO.test()

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(12345)
        }
    }

    @Test
    fun `getMovieExternalIds maps DTO to domain model correctly`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieExternalIdsResponseDTO.test(
            id = 550,
            imdbId = "tt1234567",
            wikidataId = "Q123456",
            facebookId = "testmovie",
            instagramId = "testmovieig",
            twitterId = "testmovietwitter",
        )

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(550, result.data.id)
        assertEquals("tt1234567", result.data.imdbId)
        assertEquals("Q123456", result.data.wikidataId)
        assertEquals("testmovie", result.data.facebookId)
        assertEquals("testmovieig", result.data.instagramId)
        assertEquals("testmovietwitter", result.data.twitterId)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }

    @Test
    fun `getMovieExternalIds handles partial external id data correctly`() = runTest {
        // Given
        val movieId = 123
        val dto = MovieExternalIdsResponseDTO.test(
            id = movieId,
            imdbId = "tt9876543",
            wikidataId = null,
            facebookId = "partialmovie",
            instagramId = null,
            twitterId = "partialmovietwitter",
        )

        everySuspend { moviesApi.getMovieExternalIds(movieId) }
            .returns(ApiResult.Success(dto))

        // When
        val result = MoviesRepository.test(moviesApi).getMovieExternalIds(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(123, result.data.id)
        assertEquals("tt9876543", result.data.imdbId)
        assertNull(result.data.wikidataId)
        assertEquals("partialmovie", result.data.facebookId)
        assertNull(result.data.instagramId)
        assertEquals("partialmovietwitter", result.data.twitterId)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieExternalIds(movieId)
        }
    }
}
