package dev.euryperez.tmdb.data.movies.api

import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.extensions.test
import dev.euryperez.tmdb.core.test.factory.NetworkTestFactory
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.movies.api.dtos.DatesDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.NowPlayingMoviesResponseDTO
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesApiTest : BaseTest {

    val mainCoroutineRule = MainCoroutineRule()

    val testBaseUrl = "https://api.themoviedb.org/3"
    val testApiKey = "test_api_key"

    @BeforeTest
    override fun setup() {
        mainCoroutineRule.setup()
    }

    @AfterTest
    override fun tearDown() {
        mainCoroutineRule.tearDown()
    }

    @Test
    fun `getPopularMovies returns success when API call succeeds`() = runTest {
        // Given
        val expectedResponse = """
            {
                "page": 1,
                "results": [
                    {
                        "id": 1,
                        "title": "Test Movie",
                        "overview": "A test movie",
                        "poster_path": "/poster.jpg",
                        "backdrop_path": "/backdrop.jpg",
                        "release_date": "2024-01-15",
                        "vote_average": 7.5,
                        "vote_count": 1000,
                        "popularity": 8.5,
                        "genre_ids": [28, 12],
                        "adult": false,
                        "original_language": "en",
                        "original_title": "Test Movie",
                        "video": false
                    }
                ],
                "total_pages": 10,
                "total_results": 100
            }
        """.trimIndent()

        val moviesApi = MockEngine.test(
            path = "/movie/popular",
            expectedResponse = expectedResponse,
            onRequest = { request: HttpRequestData ->
                assertEquals("application/json", request.headers["Accept"])
                assertEquals("bearer $testApiKey", request.headers["Authorization"])
                assertEquals("application/json", request.headers["Content-Type"])

                assertEquals("1", request.url.parameters["page"])
                assertEquals("en-US", request.url.parameters["language"])
            },
        ).buildMoviesApi()

        // When
        val result = moviesApi.getPopularMovies(page = 1, language = "en-US")

        // Then
        val expectedMovieListResponseDTO = MovieListResponseDTO(
            page = 1,
            totalPages = 10,
            totalResults = 100,
            results = listOf(
                MovieDTO(
                    id = 1,
                    title = "Test Movie",
                    overview = "A test movie",
                    posterPath = "/poster.jpg",
                    backdropPath = "/backdrop.jpg",
                    releaseDate = "2024-01-15",
                    voteAverage = 7.5,
                    voteCount = 1000,
                    popularity = 8.5,
                    genreIds = listOf(28, 12),
                    adult = false,
                    originalLanguage = "en",
                    originalTitle = "Test Movie",
                    video = false,
                ),
            ),
        )

        assertTrue(result is ApiResult.Success)
        assertEquals(expectedMovieListResponseDTO, result.data)
    }

    @Test
    fun `getPopularMovies returns success with custom parameters`() = runTest {
        // Given
        val expectedResponse = """
            {
                "page": 2,
                "results": [],
                "total_pages": 5,
                "total_results": 50
            }
        """.trimIndent()

        val moviesApi = MockEngine.test(
            path = "/movie/popular",
            expectedResponse = expectedResponse,
            onRequest = { request: HttpRequestData ->
                assertEquals("2", request.url.parameters["page"])
                assertEquals("es-ES", request.url.parameters["language"])
            },
        ).buildMoviesApi()

        // When
        val result = moviesApi.getPopularMovies(page = 2, language = "es-ES")

        // Then
        assertTrue(result is ApiResult.Success)
        assertEquals(2, result.data.page)
        assertEquals(5, result.data.totalPages)
    }

    @Test
    fun `getPopularMovies returns HttpError when unauthorized`() = runTest {
        // Given
        val test401Response = """{"status_code":7,"status_message":"Invalid API key"}"""

        val moviesApi = MockEngine.test(
            path = "/movie/popular",
            expectedResponse = test401Response,
            expectedStatusCode = HttpStatusCode.Unauthorized,
        ).buildMoviesApi()

        // When
        val result = moviesApi.getPopularMovies(page = 1, language = "en-US")

        // Then
        assertTrue(result is ApiResult.Error.HttpError)
        assertEquals(401, result.code)
        assertEquals(test401Response, result.message)
    }

    @Test
    fun `getPopularMovies returns HttpError when client error occurs`() = runTest {
        // Given
        val test404Response = """
                {
                    "status_code":34,
                    "status_message":"The resource you requested could not be found."
                }
        """.trimIndent()

        val moviesApi = MockEngine.test(
            path = "/movie/popular",
            expectedResponse = test404Response,
            expectedStatusCode = HttpStatusCode.NotFound,
        ).buildMoviesApi()

        // When
        val result = moviesApi.getPopularMovies(page = 1, language = "en-US")

        // Then
        assertTrue(result is ApiResult.Error.HttpError)
        assertEquals(404, result.code)
        assertEquals(test404Response, result.message)
    }

    @Test
    fun `getPopularMovies returns HttpError when server error occurs`() = runTest {
        // Given
        val test500Response = """{"error": "Internal server error"}"""

        val moviesApi = MockEngine.test(
            path = "/movie/popular",
            expectedResponse = test500Response,
            expectedStatusCode = HttpStatusCode.InternalServerError,
        ).buildMoviesApi()

        // When
        val result = moviesApi.getPopularMovies(page = 1, language = "en-US")

        // Then
        assertTrue(result is ApiResult.Error.HttpError)
        assertEquals(500, result.code)
        assertEquals(test500Response, result.message)
    }

    @Test
    fun `getPopularMovies returns NetworkError when IOException occurs`() = runTest {
        // Given
        val moviesApi = MockEngine.test(
            throwable = IOException("Network connection failed"),
        ).buildMoviesApi()

        // When
        val result = moviesApi.getPopularMovies(page = 1, language = "en-US")

        // Then
        assertTrue(result is ApiResult.Error.NetworkError)
        assertEquals("Network connection failed", result.message)
    }

    @Test
    fun `getPopularMovies returns SerializationError when response cannot be parsed`() = runTest {
        // Given
        val moviesApi = MockEngine.test(
            path = "/movie/popular",
            expectedResponse = """{"invalid": "json structure", "missing": "required fields"}""",
            expectedStatusCode = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        ).buildMoviesApi()

        // When
        val result = moviesApi.getPopularMovies(page = 1, language = "en-US")

        // Then
        assertTrue(result is ApiResult.Error.SerializationError)
        assertTrue(result.message?.contains("serial")!!)
    }

    @Test
    fun `getNowPlayingMovies returns success when API call succeeds`() = runTest {
        // Given
        val expectedResponse = """
            {
                "page": 1,
                "results": [
                    {
                        "id": 2,
                        "title": "Now Playing Movie",
                        "overview": "A currently playing movie",
                        "poster_path": "/now_playing_poster.jpg",
                        "backdrop_path": "/now_playing_backdrop.jpg",
                        "release_date": "2024-01-20",
                        "vote_average": 8.0,
                        "vote_count": 1500,
                        "popularity": 9.0,
                        "genre_ids": [28, 53],
                        "adult": false,
                        "original_language": "en",
                        "original_title": "Now Playing Movie",
                        "video": false
                    }
                ],
                "total_pages": 5,
                "total_results": 50,
                "dates": {
                    "maximum": "2024-02-15",
                    "minimum": "2024-01-01"
                }
            }
        """.trimIndent()

        val moviesApi = MockEngine.test(
            path = "/movie/now_playing",
            expectedResponse = expectedResponse,
            onRequest = { request: HttpRequestData ->
                assertEquals("application/json", request.headers["Accept"])
                assertEquals("bearer $testApiKey", request.headers["Authorization"])
                assertEquals("application/json", request.headers["Content-Type"])

                assertEquals("1", request.url.parameters["page"])
                assertEquals("en-US", request.url.parameters["language"])
            },
        ).buildMoviesApi()

        // When
        val result = moviesApi.getNowPlayingMovies(page = 1, language = "en-US")

        // Then
        val expectedNowPlayingMoviesResponseDTO = NowPlayingMoviesResponseDTO(
            page = 1,
            totalPages = 5,
            totalResults = 50,
            results = listOf(
                MovieDTO(
                    id = 2,
                    title = "Now Playing Movie",
                    overview = "A currently playing movie",
                    posterPath = "/now_playing_poster.jpg",
                    backdropPath = "/now_playing_backdrop.jpg",
                    releaseDate = "2024-01-20",
                    voteAverage = 8.0,
                    voteCount = 1500,
                    popularity = 9.0,
                    genreIds = listOf(28, 53),
                    adult = false,
                    originalLanguage = "en",
                    originalTitle = "Now Playing Movie",
                    video = false,
                ),
            ),
            dates = DatesDTO(maximum = "2024-02-15", minimum = "2024-01-01"),
        )

        assertTrue(result is ApiResult.Success)
        assertEquals(expectedNowPlayingMoviesResponseDTO, result.data)
    }

    @Test
    fun `getNowPlayingMovies returns success with custom parameters`() = runTest {
        // Given
        val expectedResponse = """
            {
                "page": 3,
                "results": [],
                "total_pages": 8,
                "total_results": 80,
                "dates": {
                    "maximum": "2024-02-20",
                    "minimum": "2024-01-05"
                }
            }
        """.trimIndent()

        val moviesApi = MockEngine.test(
            path = "/movie/now_playing",
            expectedResponse = expectedResponse,
            onRequest = { request: HttpRequestData ->
                assertEquals("3", request.url.parameters["page"])
                assertEquals("fr-FR", request.url.parameters["language"])
            },
        ).buildMoviesApi()

        // When
        val result = moviesApi.getNowPlayingMovies(page = 3, language = "fr-FR")

        // Then
        assertTrue(result is ApiResult.Success)
        assertEquals(3, result.data.page)
        assertEquals(8, result.data.totalPages)
        assertEquals("2024-02-20", result.data.dates.maximum)
        assertEquals("2024-01-05", result.data.dates.minimum)
    }

    private fun HttpClientEngine.buildMoviesApi(baseUrl: String = testBaseUrl, apiKey: String = testApiKey): MoviesApi {
        return NetworkTestFactory.httpClient(
            baseUrl = baseUrl,
            apiKey = apiKey,
            engine = this,
        ).let { MoviesApiImpl(it) }
    }
}
