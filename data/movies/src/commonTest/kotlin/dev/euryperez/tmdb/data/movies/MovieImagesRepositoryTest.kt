package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.api.MoviesApi
import dev.euryperez.tmdb.data.movies.api.dtos.MovieImagesResponseDTO
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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieImagesRepositoryTest : BaseTest {

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
    fun `getMovieImages returns success with mapped images when api call succeeds`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieImagesResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieImages(movieId, null, null) }
            .returns(ApiResult.Success(dto))

        val repository = MoviesRepositoryImpl(moviesApi = moviesApi)

        // When
        val result = repository.getMovieImages(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieImages(movieId, null, null)
        }
    }

    @Test
    fun `getMovieImages returns success with language filter when language parameter is provided`() = runTest {
        // Given
        val movieId = 550
        val language = "es"
        val dto = MovieImagesResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieImages(movieId, language, null) }
            .returns(ApiResult.Success(dto))

        val repository = MoviesRepositoryImpl(moviesApi = moviesApi)

        // When
        val result = repository.getMovieImages(movieId, language = language)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieImages(movieId, language, null)
        }
    }

    @Test
    fun `getMovieImages returns success with multiple languages when includeImageLanguage is provided`() = runTest {
        // Given
        val movieId = 550
        val includeImageLanguage = listOf("en", "es", "fr")
        val dto = MovieImagesResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieImages(movieId, null, includeImageLanguage) }
            .returns(ApiResult.Success(dto))

        val repository = MoviesRepositoryImpl(moviesApi = moviesApi)

        // When
        val result = repository.getMovieImages(movieId, includeImageLanguage = includeImageLanguage)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieImages(movieId, null, includeImageLanguage)
        }
    }

    @Test
    fun `getMovieImages returns success with both language and includeImageLanguage parameters`() = runTest {
        // Given
        val movieId = 550
        val language = "es"
        val includeImageLanguage = listOf("en", "fr")
        val dto = MovieImagesResponseDTO.test()
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieImages(movieId, language, includeImageLanguage) }
            .returns(ApiResult.Success(dto))

        val repository = MoviesRepositoryImpl(moviesApi = moviesApi)

        // When
        val result = repository.getMovieImages(
            movieId,
            language = language,
            includeImageLanguage = includeImageLanguage,
        )

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieImages(movieId, language, includeImageLanguage)
        }
    }

    @Test
    fun `getMovieImages returns success with empty image arrays when movie has no images`() = runTest {
        // Given
        val movieId = 550
        val dto = MovieImagesResponseDTO.test(
            backdrops = emptyList(),
            posters = emptyList(),
            logos = emptyList(),
        )
        val expected = dto.toDomain()

        everySuspend { moviesApi.getMovieImages(movieId, null, null) }
            .returns(ApiResult.Success(dto))

        val repository = MoviesRepositoryImpl(moviesApi = moviesApi)

        // When
        val result = repository.getMovieImages(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        assertEquals(expected, result.data)
        assertTrue(result.data.backdrops.isEmpty())
        assertTrue(result.data.posters.isEmpty())
        assertTrue(result.data.logos.isEmpty())

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieImages(movieId, null, null)
        }
    }

    @Test
    fun `getMovieImages returns failure when api call fails`() = runTest {
        // Given
        val movieId = 999999
        val errorMessage = "Movie not found"

        everySuspend { moviesApi.getMovieImages(movieId, null, null) }
            .returns(ApiResult.Error.HttpError(404, errorMessage))

        val repository = MoviesRepositoryImpl(moviesApi = moviesApi)

        // When
        val result = repository.getMovieImages(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertEquals(errorMessage, result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieImages(movieId, null, null)
        }
    }

    @Test
    fun `getMovieImages returns failure when api call throws exception`() = runTest {
        // Given
        val movieId = 550
        val exception = RuntimeException("Network error")

        everySuspend { moviesApi.getMovieImages(movieId, null, null) }
            .returns(ApiResult.Error.NetworkError("Network error"))

        val repository = MoviesRepositoryImpl(moviesApi = moviesApi)

        // When
        val result = repository.getMovieImages(movieId)

        // Then
        assertTrue(result is DataResult.Failure)
        assertNotNull(result.message)

        verifySuspend(VerifyMode.exactly(1)) {
            moviesApi.getMovieImages(movieId, null, null)
        }
    }
}
