package dev.euryperez.tmdb.integration

import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.requireApiKey
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
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
 * End-to-end integration test that verifies TMDB API connectivity and basic functionality.
 *
 * This test focuses on:
 * - Real HTTP client integration with TMDB API
 * - Authentication and error handling
 * - Basic API connectivity and response parsing
 * - Consumer-facing error scenarios
 *
 * To run this test with a real API key:
 * 1. Get a TMDB API key from https://www.themoviedb.org/settings/api
 * 2. Set environment variable: export TMDB_API_KEY="your_api_key_here"
 * 3. Run: ./gradlew :integration-tests:jvmTest --tests "*TmdbApiEndToEndTest*"
 *
 * Without an API key, the test will fail with a clear error message.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TmdbApiEndToEndTest : BaseTest {

    private val mainCoroutineRule = MainCoroutineRule()

    @BeforeTest
    override fun setup() {
        mainCoroutineRule.setup()
    }

    @AfterTest
    override fun tearDown() {
        mainCoroutineRule.tearDown()
    }

    @Test
    fun `consumer can use library to retrieve real movie data from TMDB API`() = runTest(timeout = 30.seconds) {
        // Get API key from environment - this demonstrates real-world usage
        val apiKey = requireApiKey()

        println("🚀 RUNNING INTEGRATION TEST with real TMDB API")
        println("   This test makes actual network calls to TMDB...")

        // GIVEN: Consumer creates repository using the public API (exactly how they will use it)
        val repository = MoviesRepository.factory(apiKey = apiKey)

        // WHEN: Consumer makes a request for popular movies
        val result = repository.getPopularMovies(page = 1, language = "en-US")

        // THEN: Consumer receives real movie data from TMDB
        assertTrue(result is DataResult.Success, "Expected successful API response, got: $result")

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Should receive actual popular movies from TMDB")

        // Verify the real data structure that consumers will work with
        val firstMovie = movies.first()
        assertNotNull(firstMovie.id, "Movie should have ID")
        assertNotNull(firstMovie.title, "Movie should have title")
        assertTrue(firstMovie.id > 0, "Movie ID should be positive: ${firstMovie.id}")
        assertTrue(firstMovie.title.isNotBlank(), "Movie title should not be blank: '${firstMovie.title}'")
        assertTrue(firstMovie.voteAverage >= 0.0, "Vote average should be non-negative: ${firstMovie.voteAverage}")
        assertTrue(firstMovie.voteCount >= 0, "Vote count should be non-negative: ${firstMovie.voteCount}")
        assertTrue(firstMovie.popularity > 0.0, "Popularity should be positive: ${firstMovie.popularity}")

        // Verify this is real TMDB data (popular movies should have reasonable metrics)
        assertTrue(
            firstMovie.voteCount > 0,
            "Popular movies should have vote counts: ${firstMovie.voteCount}",
        )
        assertTrue(firstMovie.popularity > 10.0, "Popular movies should have high popularity: ${firstMovie.popularity}")

        println("✅ INTEGRATION TEST PASSED!")
        println("   Successfully retrieved ${movies.size} popular movies from TMDB")
        println("   First movie: '${firstMovie.title}' (${firstMovie.releaseDate})")
        println("   Movie ID: ${firstMovie.id}")
        println("   Rating: ${firstMovie.voteAverage}/10 (${firstMovie.voteCount} votes)")
        println("   Popularity: ${firstMovie.popularity}")
        println("")
        println("   This confirms:")
        println("   ✓ HTTP client is properly configured")
        println("   ✓ Authentication works with TMDB API")
        println("   ✓ Network requests execute successfully")
        println("   ✓ JSON responses are correctly deserialized")
        println("   ✓ DTOs are properly mapped to domain models")
        println("   ✓ Repository layer works end-to-end")
        println("   ✓ Consumer API is functional and ready for client apps")
    }

    @Test
    fun `invalid API key produces proper error for consumers`() = runTest(timeout = 15.seconds) {
        println("🔐 TESTING AUTHENTICATION ERROR HANDLING")

        // GIVEN: Consumer uses invalid API key (common configuration mistake)
        val repository = MoviesRepository.factory(apiKey = "invalid_key_12345")

        // WHEN: Consumer attempts API call
        val result = repository.getPopularMovies()

        // THEN: Consumer receives clear authentication error
        assertTrue(result is DataResult.Failure, "Expected authentication failure")

        val errorMessage = result.message
        assertNotNull(errorMessage, "Error message should be provided to consumer")
        assertTrue(
            errorMessage.contains("401") ||
                errorMessage.contains("Unauthorized", ignoreCase = true) ||
                errorMessage.contains("invalid", ignoreCase = true),
            "Error should indicate authentication issue, got: '$errorMessage'",
        )

        println("✅ AUTHENTICATION ERROR TEST PASSED!")
        println("   Error message: $errorMessage")
        println("   This confirms consumers get clear error messages for auth issues")
    }
}
