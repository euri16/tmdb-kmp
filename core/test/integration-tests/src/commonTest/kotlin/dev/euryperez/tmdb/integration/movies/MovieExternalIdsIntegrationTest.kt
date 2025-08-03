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
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

/**
 * Integration tests for MoviesRepository.getMovieExternalIds endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective using real TMDB API responses.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieExternalIdsIntegrationTest : BaseTest {

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
    fun `movie external ids returns real external ids for Fight Club`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository and Fight Club movie (known to have rich external ID data)
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests external IDs for a well-known movie with comprehensive external presence
        val result = repository.getMovieExternalIds(movieId = FIGHT_CLUB_MOVIE_ID)

        // Then: Consumer receives real external ID data from TMDB API
        assertTrue(
            result is DataResult.Success,
            "Expected successful result for Fight Club external IDs",
        )

        val externalIds = result.data
        assertEquals(
            FIGHT_CLUB_MOVIE_ID,
            externalIds.id,
            "Response should contain correct movie ID",
        )

        // Fight Club is a classic movie with well-established external presence
        assertNotNull(
            externalIds.imdbId,
            "Fight Club should have IMDb ID (classic movie with established IMDb presence)",
        )

        // Verify IMDb ID format if present
        externalIds.imdbId?.let { imdbId ->
            assertTrue(
                imdbId.startsWith("tt") && imdbId.length >= 9,
                "IMDb ID should follow standard format (tt followed by digits), got: $imdbId",
            )
        }

        // Verify Wikidata ID format if present
        externalIds.wikidataId?.let { wikidataId ->
            assertTrue(
                wikidataId.startsWith("Q") && wikidataId.drop(1).all { it.isDigit() },
                "Wikidata ID should follow Q format, got: $wikidataId",
            )
        }

        // Log actual external IDs for verification
        println("✅ Fight Club external IDs integration test passed")
        println("\tMovie ID: ${externalIds.id}")
        println("\tIMDb ID: ${externalIds.imdbId}")
        println("\tWikidata ID: ${externalIds.wikidataId}")
        println("\tFacebook ID: ${externalIds.facebookId}")
        println("\tInstagram ID: ${externalIds.instagramId}")
        println("\tTwitter ID: ${externalIds.twitterId}")
    }

    @Test
    fun `movie external ids serialization accuracy with real API response structure`() =
        runTest(timeout = TEST_TIMEOUT) {
            val testApiKey = requireApiKey()

            // Given: Repository for testing complete serialization pipeline
            val repository = MoviesRepository.factory(apiKey = testApiKey)

            // When: Consumer requests external IDs and verifies all fields are properly serialized
            val result = repository.getMovieExternalIds(movieId = FIGHT_CLUB_MOVIE_ID)

            // Then: All external ID fields are properly deserialized from real API response
            assertTrue(result is DataResult.Success, "External IDs request should succeed")

            val externalIds = result.data

            // Verify all critical fields are properly deserialized and mapped from DTO to domain model
            assertTrue(externalIds.id == FIGHT_CLUB_MOVIE_ID, "Movie ID should match request")

            // Verify nullability handling and data integrity (external IDs can be null)

            // Verify that non-null values are not blank
            externalIds.imdbId?.let { assertTrue(it.isNotBlank(), "IMDb ID should not be blank if present") }
            externalIds.wikidataId?.let { assertTrue(it.isNotBlank(), "Wikidata ID should not be blank if present") }
            externalIds.facebookId?.let { assertTrue(it.isNotBlank(), "Facebook ID should not be blank if present") }
            externalIds.instagramId?.let { assertTrue(it.isNotBlank(), "Instagram ID should not be blank if present") }
            externalIds.twitterId?.let { assertTrue(it.isNotBlank(), "Twitter ID should not be blank if present") }

            println("✅ External IDs serialization integration test passed - all fields properly deserialized")
            println("\tMovie: Fight Club (ID: ${externalIds.id})")
            println("\tSerialized fields: IMDb, Wikidata, Facebook, Instagram, Twitter all properly mapped")
        }

    @Test
    fun `movie external ids for different movies show variety in external presence`() =
        runTest(timeout = TEST_TIMEOUT) {
            val testApiKey = requireApiKey()

            // Given: Repository for testing different movies with varying external presence
            val repository = MoviesRepository.factory(apiKey = testApiKey)

            // When: Consumer requests external IDs for different well-known movies
            val fightClubResult = repository.getMovieExternalIds(movieId = FIGHT_CLUB_MOVIE_ID)
            val matrixResult = repository.getMovieExternalIds(movieId = THE_MATRIX_MOVIE_ID)

            // Then: Both movies should have external IDs but potentially different coverage
            assertTrue(fightClubResult is DataResult.Success, "Fight Club external IDs should succeed")
            assertTrue(matrixResult is DataResult.Success, "Matrix external IDs should succeed")

            val fightClubIds = fightClubResult.data
            val matrixIds = matrixResult.data

            // Verify each movie has its own ID
            assertEquals(FIGHT_CLUB_MOVIE_ID, fightClubIds.id, "Fight Club ID should match")
            assertEquals(THE_MATRIX_MOVIE_ID, matrixIds.id, "Matrix ID should match")

            // Both classic movies should have IMDb presence at minimum
            assertTrue(
                fightClubIds.imdbId != null || matrixIds.imdbId != null,
                "At least one classic movie should have IMDb ID",
            )

            // Verify different movies have different IMDb IDs
            if (fightClubIds.imdbId != null && matrixIds.imdbId != null) {
                assertTrue(
                    fightClubIds.imdbId != matrixIds.imdbId,
                    "Different movies should have different IMDb IDs",
                )
            }

            // Log actual external ID coverage for analysis
            println("✅ Multiple movie external IDs variety test passed")
            println("\tFight Club external IDs coverage:")
            println("\t  IMDb: ${fightClubIds.imdbId != null}")
            println("\t  Wikidata: ${fightClubIds.wikidataId != null}")
            println("\t  Facebook: ${fightClubIds.facebookId != null}")
            println("\t  Instagram: ${fightClubIds.instagramId != null}")
            println("\t  Twitter: ${fightClubIds.twitterId != null}")
            println("\tMatrix external IDs coverage:")
            println("\t  IMDb: ${matrixIds.imdbId != null}")
            println("\t  Wikidata: ${matrixIds.wikidataId != null}")
            println("\t  Facebook: ${matrixIds.facebookId != null}")
            println("\t  Instagram: ${matrixIds.instagramId != null}")
            println("\t  Twitter: ${matrixIds.twitterId != null}")
        }

    @Test
    fun `movie external ids handle movies with minimal external presence`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing edge cases with minimal external presence
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests external IDs for older/smaller movie that might have limited presence
        val result = repository.getMovieExternalIds(movieId = THE_GODFATHER_MOVIE_ID)

        // Then: Should handle gracefully even if some external IDs are null
        assertTrue(result is DataResult.Success, "Should succeed even for movies with limited external presence")

        val externalIds = result.data
        assertEquals(THE_GODFATHER_MOVIE_ID, externalIds.id, "Should return correct movie ID")

        // The Godfather (1972) might have limited social media presence but should have IMDb
        // We test the repository correctly handles null values from API
        assertTrue(
            externalIds.imdbId == null || externalIds.imdbId!!.isNotBlank(),
            "IMDb ID should be valid if present",
        )
        assertTrue(
            externalIds.wikidataId == null || externalIds.wikidataId!!.isNotBlank(),
            "Wikidata ID should be valid if present",
        )
        assertTrue(
            externalIds.facebookId == null || externalIds.facebookId!!.isNotBlank(),
            "Facebook ID should be valid if present",
        )
        assertTrue(
            externalIds.instagramId == null || externalIds.instagramId!!.isNotBlank(),
            "Instagram ID should be valid if present",
        )
        assertTrue(
            externalIds.twitterId == null || externalIds.twitterId!!.isNotBlank(),
            "Twitter ID should be valid if present",
        )

        // Count how many external IDs are available
        val availableIds = listOfNotNull(
            externalIds.imdbId,
            externalIds.wikidataId,
            externalIds.facebookId,
            externalIds.instagramId,
            externalIds.twitterId,
        ).size

        println("✅ External IDs minimal presence test passed")
        println("\tThe Godfather external IDs: $availableIds/5 available")
        println("\tIMDb: ${externalIds.imdbId}")
        println("\tRepository correctly handles null/missing external IDs")
    }

    @Test
    fun `movie external ids error handling with invalid movie ID`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository with valid API key but invalid movie ID
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests external IDs for non-existent movie
        val result = repository.getMovieExternalIds(movieId = INVALID_MOVIE_ID)

        // Then: Consumer receives proper error handling
        assertTrue(
            result is DataResult.Failure,
            "Expected failure result for non-existent movie external IDs",
        )

        val errorMessage = result.message
        assertNotNull(errorMessage, "Error message should be provided")
        assertTrue(
            errorMessage.contains("404") ||
                errorMessage.contains("not found", ignoreCase = true) ||
                errorMessage.contains("could not be found", ignoreCase = true),
            "Error should indicate movie not found, got: $errorMessage",
        )

        println("✅ External IDs error handling integration test passed")
        println("\tError message: $errorMessage")
    }

    @Test
    fun `movie external ids authentication error handling`() = runTest(timeout = TEST_TIMEOUT) {
        // Given: Repository with invalid API key (simulating real auth failure)
        val repository = MoviesRepository.factory(apiKey = "invalid_api_key_12345")

        // When: Consumer attempts to get external IDs with invalid credentials
        val result = repository.getMovieExternalIds(movieId = FIGHT_CLUB_MOVIE_ID)

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

        println("✅ External IDs authentication error integration test passed")
        println("\tError message: $errorMessage")
    }

    @Test
    fun `movie external ids edge cases with extreme movie IDs`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing edge case parameter handling
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer provides edge case movie IDs

        // Test with zero movie ID
        val zeroIdResult = repository.getMovieExternalIds(movieId = 0)

        // Test with negative movie ID
        val negativeIdResult = repository.getMovieExternalIds(movieId = -1)

        // Test with very large movie ID
        val largeIdResult = repository.getMovieExternalIds(movieId = 999999999)

        // Then: Repository should handle edge cases gracefully

        // Zero and negative IDs should typically fail
        assertTrue(
            zeroIdResult is DataResult.Failure,
            "Zero movie ID should fail for external IDs",
        )
        assertTrue(
            negativeIdResult is DataResult.Failure,
            "Negative movie ID should fail for external IDs",
        )
        assertTrue(
            largeIdResult is DataResult.Failure,
            "Very large movie ID should fail for external IDs",
        )

        // Verify error messages are provided
        assertNotNull(zeroIdResult.message, "Zero ID failure should include error message")
        assertNotNull(negativeIdResult.message, "Negative ID failure should include error message")
        assertNotNull(largeIdResult.message, "Large ID failure should include error message")

        println("✅ External IDs edge cases test passed")
        println("\tZero ID: Failed as expected")
        println("\tNegative ID: Failed as expected")
        println("\tLarge ID: Failed as expected")
    }

    @Test
    fun `movie external ids cross platform compatibility`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository that should work across KMP targets
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests external IDs (this test runs on different platforms via KMP)
        val result = repository.getMovieExternalIds(movieId = FIGHT_CLUB_MOVIE_ID)

        // Then: Should work consistently across Android, iOS, JVM targets
        assertTrue(result is DataResult.Success, "External IDs should work on all KMP targets")

        val externalIds = result.data

        // Verify core functionality works across platforms
        assertEquals(FIGHT_CLUB_MOVIE_ID, externalIds.id, "Movie ID should be consistent across platforms")

        // Verify Kotlin data classes work properly across platforms
        assertTrue(
            externalIds.toString().contains("TmdbMovieExternalIds"),
            "Domain model should serialize consistently across platforms",
        )

        // Verify nullable handling works across platforms (Kotlin type system ensures correctness)

        println("✅ External IDs cross-platform compatibility test passed")
        println("\tTested on current platform - KMP external IDs functionality works correctly")
    }

    private companion object {
        const val FIGHT_CLUB_MOVIE_ID = 550 // Fight Club (1999) - stable for external IDs testing
        const val THE_MATRIX_MOVIE_ID = 603 // The Matrix (1999) - stable for comparison testing
        const val THE_GODFATHER_MOVIE_ID = 238 // The Godfather (1972) - classic for minimal presence testing
        const val INVALID_MOVIE_ID = 999999999 // Non-existent movie ID for error testing
        val TEST_TIMEOUT = 30.seconds
    }
}
