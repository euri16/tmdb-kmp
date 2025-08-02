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
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

/**
 * Integration tests for MoviesRepository.getTopRatedMovies endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TopRatedMoviesIntegrationTest : BaseTest {

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
    fun `top rated movies endpoint returns real highly rated movies`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository configured for production use
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests top rated movies
        val result = repository.getTopRatedMovies(page = 1, language = "en-US")

        // Then: Consumer receives genuinely top-rated movies
        assertTrue(
            result is DataResult.Success,
            "Expected successful result for top rated movies",
        )

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Top rated movies should not be empty")

        // Verify these are actually highly rated movies
        movies.forEach { movie ->
            assertTrue(
                movie.voteAverage > 7.0,
                "Top rated movies should have high ratings, got ${movie.voteAverage} for '${movie.title}'",
            )
            assertTrue(
                movie.voteCount > 100,
                "Top rated movies should have substantial vote counts, got ${movie.voteCount} for '${movie.title}'",
            )
        }

        val averageRating = movies.map { it.voteAverage }.average()
        assertTrue(
            averageRating > 8.0,
            "Average rating of top movies should be very high, got $averageRating",
        )

        println("✅ Top rated integration test passed - retrieved ${movies.size} top rated movies")
        println("\tAverage rating: $averageRating")
        println(
            "\tHighest rated: '${movies.maxByOrNull { it.voteAverage }?.title}' " +
                "(${movies.maxByOrNull { it.voteAverage }?.voteAverage})",
        )
    }

    @Test
    fun `top rated movies pagination works correctly`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing pagination
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests different pages of top rated movies
        val page1Result = repository.getTopRatedMovies(page = 1)
        val page2Result = repository.getTopRatedMovies(page = 2)

        // Then: Both pages return different highly rated movies
        assertTrue(page1Result is DataResult.Success, "Page 1 should succeed")
        assertTrue(page2Result is DataResult.Success, "Page 2 should succeed")

        val page1Movies = page1Result.data
        val page2Movies = page2Result.data

        assertTrue(page1Movies.isNotEmpty(), "Page 1 should have movies")
        assertTrue(page2Movies.isNotEmpty(), "Page 2 should have movies")

        // Verify pages contain different movies
        val page1Ids = page1Movies.map { it.id }.toSet()
        val page2Ids = page2Movies.map { it.id }.toSet()
        assertTrue(
            page1Ids.intersect(page2Ids).isEmpty(),
            "Different pages should contain different movies",
        )

        // Verify both pages maintain high rating standards
        val page1AverageRating = page1Movies.map { it.voteAverage }.average()
        val page2AverageRating = page2Movies.map { it.voteAverage }.average()

        assertTrue(
            page1AverageRating > 7.5,
            "Page 1 should maintain high rating average, got $page1AverageRating",
        )
        assertTrue(
            page2AverageRating > 7.5,
            "Page 2 should maintain high rating average, got $page2AverageRating",
        )

        println("✅ Top rated movies pagination test passed")
        println("\tPage 1: ${page1Movies.size} movies, avg rating: $page1AverageRating")
        println("\tPage 2: ${page2Movies.size} movies, avg rating: $page2AverageRating")
    }

    @Test
    fun `top rated movies localization works with different languages`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing localization
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests top rated movies in different languages
        val englishResult = repository.getTopRatedMovies(page = 1, language = "en-US")
        val germanResult = repository.getTopRatedMovies(page = 1, language = "de-DE")

        // Then: Both language versions are returned successfully
        assertTrue(englishResult is DataResult.Success, "English version should succeed")
        assertTrue(germanResult is DataResult.Success, "German version should succeed")

        val englishMovies = englishResult.data
        val germanMovies = germanResult.data

        assertTrue(englishMovies.isNotEmpty(), "English movies should not be empty")
        assertTrue(germanMovies.isNotEmpty(), "German movies should not be empty")

        // Verify both maintain high rating standards regardless of language
        englishMovies.forEach { movie ->
            assertTrue(
                movie.voteAverage > 7.0,
                "English top rated movies should have high ratings, got ${movie.voteAverage}",
            )
        }

        germanMovies.forEach { movie ->
            assertTrue(
                movie.voteAverage > 7.0,
                "German top rated movies should have high ratings, got ${movie.voteAverage}",
            )
        }

        println("✅ Top rated movies localization test passed")
        println("\tEnglish movies: ${englishMovies.size}")
        println("\tGerman movies: ${germanMovies.size}")
        println("\tSample English: '${englishMovies.first().title}' (${englishMovies.first().voteAverage})")
        println("\tSample German: '${germanMovies.first().title}' (${germanMovies.first().voteAverage})")
    }

    @Test
    fun `top rated movies have substantial vote counts indicating credibility`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing vote count credibility
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests top rated movies
        val result = repository.getTopRatedMovies(page = 1, language = "en-US")

        // Then: Movies should have substantial vote counts to ensure credible ratings
        assertTrue(result is DataResult.Success, "Should succeed getting top rated movies")

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Should have top rated movies")

        // Verify vote count distribution indicates credible ratings
        val averageVoteCount = movies.map { it.voteCount }.average()
        val minVoteCount = movies.minOf { it.voteCount }
        val maxVoteCount = movies.maxOf { it.voteCount }

        assertTrue(
            averageVoteCount > 500,
            "Top rated movies should have substantial average vote count, got $averageVoteCount",
        )
        assertTrue(
            minVoteCount > 50,
            "Even lowest voted top rated movie should have substantial votes, got $minVoteCount",
        )

        // Find movies with very high vote counts (popular and highly rated)
        val highlyVotedMovies = movies.filter { it.voteCount > 1000 }
        assertTrue(
            highlyVotedMovies.isNotEmpty(),
            "Should have some movies with very high vote counts indicating broad appeal",
        )

        println("✅ Top rated movies vote count validation test passed")
        println("\tAverage vote count: $averageVoteCount")
        println("\tVote count range: $minVoteCount to $maxVoteCount")
        println("\tHighly voted movies (>1000 votes): ${highlyVotedMovies.size}")
    }

    private companion object {
        val TEST_TIMEOUT = 30.seconds
    }
}
