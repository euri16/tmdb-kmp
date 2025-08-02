package dev.euryperez.tmdb.integration.movies

import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.core.utils.extensions.getEnvironmentVariable
import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.data.movies.MoviesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

/**
 * Integration tests for MoviesRepository.getPopularMovies endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PopularMoviesIntegrationTest : BaseTest {

    private val mainCoroutineRule = MainCoroutineRule()

    private val apiKey: String? by lazy { getEnvironmentVariable("TMDB_API_KEY") }

    @BeforeTest
    override fun setup() {
        mainCoroutineRule.setup()
    }

    @AfterTest
    override fun tearDown() {
        mainCoroutineRule.tearDown()
    }

    @Test
    fun `consumer can create repository and make successful API calls`() = runTest(timeout = TEST_TIMEOUT) {
        // Skip test if no API key is available
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Consumer creates repository using the public API
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer calls a popular movies endpoint
        val result = repository.getPopularMovies(page = 1, language = "en-US")

        // Then: Consumer receives successful response with actual movie data
        assertTrue(result is DataResult.Success, "Expected successful result but got: $result")

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Expected movies list to not be empty")

        // Verify real movie data structure that consumers will receive
        val firstMovie = movies.first()
        assertNotNull(firstMovie.id, "Movie ID should not be null")
        assertNotNull(firstMovie.title, "Movie title should not be null")
        assertTrue(firstMovie.id > 0, "Movie ID should be positive")
        assertTrue(firstMovie.title.isNotBlank(), "Movie title should not be blank")
        assertTrue(firstMovie.voteAverage >= 0.0, "Vote average should be non-negative")
        assertTrue(firstMovie.voteCount >= 0, "Vote count should be non-negative")

        println("✅ Integration test passed - retrieved ${movies.size} popular movies")
        println("\tFirst movie: '${firstMovie.title}' (ID: ${firstMovie.id}, Rating: ${firstMovie.voteAverage})")
    }

    @Test
    fun `pagination works correctly across multiple pages`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing pagination
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests different pages of popular movies
        val page1Result = repository.getPopularMovies(page = 1)
        val page2Result = repository.getPopularMovies(page = 2)

        // Then: Both pages return different movies
        assertTrue(page1Result is DataResult.Success, "Page 1 should succeed")
        assertTrue(page2Result is DataResult.Success, "Page 2 should succeed")

        val page1Movies = page1Result.data
        val page2Movies = page2Result.data

        assertTrue(page1Movies.isNotEmpty(), "Page 1 should have movies")
        assertTrue(page2Movies.isNotEmpty(), "Page 2 should have movies")

        // Verify pages contain different movies (no overlap expected for popular movies)
        val page1Ids = page1Movies.map { it.id }.toSet()
        val page2Ids = page2Movies.map { it.id }.toSet()
        assertTrue(
            page1Ids.intersect(page2Ids).isEmpty(),
            "Different pages should contain different movies",
        )

        println("✅ Pagination integration test passed")
        println("\tPage 1: ${page1Movies.size} movies, first ID: ${page1Movies.first().id}")
        println("\tPage 2: ${page2Movies.size} movies, first ID: ${page2Movies.first().id}")
    }

    @Test
    fun `consumer workflow - complete movie discovery and details retrieval`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Consumer wants to discover and explore movies (typical usage pattern)
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer follows typical workflow

        // Step 1: Discover popular movies
        val popularResult = repository.getPopularMovies(page = 1, language = "en-US")
        assertTrue(
            popularResult is DataResult.Success,
            "Popular movies discovery should succeed",
        )

        val popularMovies = popularResult.data
        assertTrue(popularMovies.isNotEmpty(), "Should discover popular movies")

        // Step 2: Get details for first discovered movie
        val firstMovie = popularMovies.first()
        val detailsResult = repository.getMovieDetails(movieId = firstMovie.id)
        assertTrue(
            detailsResult is DataResult.Success,
            "Movie details retrieval should succeed",
        )

        val movieDetails = detailsResult.data
        assertTrue(movieDetails.id == firstMovie.id, "Details should match discovered movie")

        // Step 3: Get alternative titles for international audience
        val alternativeTitlesResult =
            repository.getMovieAlternativeTitles(movieId = firstMovie.id)
        assertTrue(
            alternativeTitlesResult is DataResult.Success,
            "Alternative titles should be retrievable",
        )

        val alternativeTitles = alternativeTitlesResult.data
        assertTrue(
            alternativeTitles.id == firstMovie.id,
            "Alternative titles should match movie",
        )

        // Then: Consumer has complete movie information for their application
        println("✅ Complete consumer workflow integration test passed")
        println("\tDiscovered ${popularMovies.size} popular movies")
        println(
            "\tRetrieved details for: '${movieDetails.title}' (${movieDetails.releaseDate})",
        )
        println("\tFound ${alternativeTitles.titles.size} alternative titles")
        println(
            "\tMovie rating: ${movieDetails.voteAverage}/10 (${movieDetails.voteCount} votes)",
        )

        // Verify data consistency across different endpoints
        assertTrue(firstMovie.title == movieDetails.title, "Title should be consistent")
        assertTrue(firstMovie.id == movieDetails.id, "ID should be consistent")
        // Vote averages should be close (within 1.0 point) as data may vary slightly between endpoints/cache
        val voteAverageDiff = kotlin.math.abs(firstMovie.voteAverage - movieDetails.voteAverage)
        assertTrue(
            voteAverageDiff <= 1.0,
            "Vote averages should be similar (within 1.0 point), got ${firstMovie.voteAverage} vs ${movieDetails.voteAverage}, diff: $voteAverageDiff",
        )
    }

    private companion object {
        val TEST_TIMEOUT = 30.seconds
    }
}
