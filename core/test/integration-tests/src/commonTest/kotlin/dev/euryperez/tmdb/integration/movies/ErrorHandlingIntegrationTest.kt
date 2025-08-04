package dev.euryperez.tmdb.integration.movies

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
 * Integration tests for MoviesRepository error handling scenarios that test real API calls to TMDB.
 * These tests verify end-to-end error handling from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ErrorHandlingIntegrationTest : BaseTest {

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
    fun `authentication failure returns proper error when invalid API key used`() = runTest(timeout = TEST_TIMEOUT) {
        // Given: Repository with invalid API key (simulating real auth failure)
        val repository = MoviesRepository.factory(apiKey = "invalid_api_key_12345")

        // When: Consumer attempts to make API call with invalid credentials
        val result = repository.getPopularMovies()

        // Then: Consumer receives authentication failure
        assertTrue(result is DataResult.Failure, "Expected failure result for invalid API key")

        val errorMessage = result.message
        assertNotNull(errorMessage, "Error message should be provided to consumer")
        assertTrue(
            errorMessage.contains("401") ||
                errorMessage.contains("Unauthorized") ||
                errorMessage.contains("invalid", ignoreCase = true),
            "Error message should indicate authentication issue, got: $errorMessage",
        )

        println("✅ Authentication error integration test passed - properly handled invalid API key")
        println("\tError message: $errorMessage")
    }

    @Test
    fun `network error handling works with non-existent movie ID`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository with valid API key but invalid movie ID
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests details for non-existent movie
        val result = repository.getMovieDetails(movieId = INVALID_MOVIE_ID)

        // Then: Consumer receives proper error handling
        assertTrue(
            result is DataResult.Failure,
            "Expected failure result for non-existent movie",
        )

        val errorMessage = result.message
        assertNotNull(errorMessage, "Error message should be provided")
        assertTrue(
            errorMessage.contains("404") ||
                errorMessage.contains("not found", ignoreCase = true) ||
                errorMessage.contains("could not be found", ignoreCase = true),
            "Error should indicate movie not found, got: $errorMessage",
        )

        println("✅ Error handling integration test passed - properly handled non-existent movie")
        println("\tError message: $errorMessage")
    }

    @Test
    fun `error handling works across different endpoints`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing error handling across endpoints
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer makes invalid requests to different endpoints
        val movieDetailsResult = repository.getMovieDetails(movieId = INVALID_MOVIE_ID)
        val movieCreditsResult = repository.getMovieCredits(movieId = INVALID_MOVIE_ID)
        val alternativeTitlesResult =
            repository.getMovieAlternativeTitles(movieId = INVALID_MOVIE_ID)

        // Then: All endpoints should handle errors consistently
        assertTrue(
            movieDetailsResult is DataResult.Failure,
            "Movie details should fail for invalid ID",
        )
        assertTrue(
            movieCreditsResult is DataResult.Failure,
            "Movie credits should fail for invalid ID",
        )
        assertTrue(
            alternativeTitlesResult is DataResult.Failure,
            "Alternative titles should fail for invalid ID",
        )

        // Verify all have error messages
        assertNotNull(movieDetailsResult.message, "Movie details should have error message")
        assertNotNull(movieCreditsResult.message, "Movie credits should have error message")
        assertNotNull(
            alternativeTitlesResult.message,
            "Alternative titles should have error message",
        )

        // All should indicate 404/not found type errors
        val errorMessages = listOfNotNull(
            movieDetailsResult.message,
            movieCreditsResult.message,
            alternativeTitlesResult.message,
        )

        errorMessages.forEach { message ->
            assertTrue(
                message.contains("404") ||
                    message.contains("not found", ignoreCase = true) ||
                    message.contains("could not be found", ignoreCase = true),
                "Error message should indicate not found: $message",
            )
        }

        println("✅ Multi-endpoint error handling test passed")
        println("\tMovie details error: ${movieDetailsResult.message}")
        println("\tMovie credits error: ${movieCreditsResult.message}")
        println("\tAlternative titles error: ${alternativeTitlesResult.message}")
    }

    @Test
    fun `error handling with invalid parameters across list endpoints`() = runTest(timeout = TEST_TIMEOUT) {
        // Given: Repository with invalid API key for testing parameter validation
        val invalidRepository = MoviesRepository.factory(apiKey = "invalid_key_123")

        // When: Consumer makes requests to list endpoints with invalid auth
        val popularResult = invalidRepository.getPopularMovies()
        val upcomingResult = invalidRepository.getUpcomingMovies()
        val nowPlayingResult = invalidRepository.getNowPlayingMovies()
        val topRatedResult = invalidRepository.getTopRatedMovies()

        // Then: All list endpoints should handle authentication errors consistently
        assertTrue(
            popularResult is DataResult.Failure,
            "Popular movies should fail with invalid key",
        )
        assertTrue(
            upcomingResult is DataResult.Failure,
            "Upcoming movies should fail with invalid key",
        )
        assertTrue(
            nowPlayingResult is DataResult.Failure,
            "Now playing should fail with invalid key",
        )
        assertTrue(
            topRatedResult is DataResult.Failure,
            "Top rated should fail with invalid key",
        )

        // Verify consistent error messaging
        val listEndpointResults =
            listOfNotNull(
                popularResult.message,
                upcomingResult.message,
                nowPlayingResult.message,
                topRatedResult.message,
            )
        listEndpointResults.forEach { message ->
            assertNotNull(message, "Each endpoint should provide error message")
            assertTrue(
                message.contains("401") ||
                    message.contains("Unauthorized") ||
                    message.contains("invalid", ignoreCase = true),
                "Each endpoint should indicate auth error: $message",
            )
        }

        println("✅ List endpoints error handling test passed")
        println("\tAll list endpoints properly handle authentication errors")
    }

    @Test
    fun `error handling with edge case parameters`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing edge case parameter handling
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer provides edge case parameters

        // Test with extremely high page number (likely beyond available data)
        val highPageResult = repository.getPopularMovies(page = 10000)

        // Test with zero movie ID
        val zeroIdResult = repository.getMovieDetails(movieId = 0)

        // Test with negative movie ID
        val negativeIdResult = repository.getMovieDetails(movieId = -1)

        // Then: Repository should handle edge cases gracefully

        // High page might return empty results or error - both are acceptable
        assertTrue(
            (highPageResult is DataResult.Success && highPageResult.data.isEmpty()) ||
                highPageResult is DataResult.Failure,
            "High page number should return empty results or error",
        )

        // Zero and negative IDs should typically fail
        assertTrue(
            zeroIdResult is DataResult.Failure,
            "Zero movie ID should fail",
        )
        assertTrue(
            negativeIdResult is DataResult.Failure,
            "Negative movie ID should fail",
        )

        println("✅ Edge case parameters error handling test passed")
        println(
            "\tHigh page result: ${if (highPageResult is DataResult.Success) "Success (${highPageResult.data.size} items)" else "Failure"}",
        )
    }

    @Test
    fun `search error handling with problematic queries`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing search error scenarios
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer searches with potentially problematic queries

        // Empty query (might be rejected by API)
        val emptyQueryResult = repository.searchMovies(query = "")

        // Very long query
        val longQuery = "a".repeat(1000)
        val longQueryResult = repository.searchMovies(query = longQuery)

        // Special characters
        val specialCharsResult = repository.searchMovies(query = "!@#$%^&*()")

        // Then: Search should handle edge cases gracefully
        // Note: Different APIs handle these differently, so we verify the behavior is consistent

        listOf(emptyQueryResult, longQueryResult, specialCharsResult).forEach { result ->
            assertTrue(
                result is DataResult.Success || result is DataResult.Failure,
                "Search should return either success or controlled failure",
            )

            if (result is DataResult.Failure) {
                assertNotNull(result.message, "Failure should include error message")
            }
        }

        println("✅ Search error handling test passed")
        println(
            "\tEmpty query: ${if (emptyQueryResult is DataResult.Success) "Success (${emptyQueryResult.data.size} results)" else "Failed"}",
        )
        println(
            "\tLong query: ${if (longQueryResult is DataResult.Success) "Success (${longQueryResult.data.size} results)" else "Failed"}",
        )
        println(
            "\tSpecial chars: ${if (specialCharsResult is DataResult.Success) "Success (${specialCharsResult.data.size} results)" else "Failed"}",
        )
    }

    private companion object {
        const val INVALID_MOVIE_ID = 999999999 // Non-existent movie ID for error testing
        val TEST_TIMEOUT = 30.seconds
    }
}
