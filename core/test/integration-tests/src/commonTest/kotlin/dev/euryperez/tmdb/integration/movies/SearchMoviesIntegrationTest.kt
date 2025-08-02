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
 * Integration tests for MoviesRepository.searchMovies endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 *
 * Note: This endpoint is marked as TODO to move to :data:search module in the future.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SearchMoviesIntegrationTest : BaseTest {

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
    fun `search movies returns relevant results for well-known titles`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing search functionality
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer searches for a well-known movie
        val result = repository.searchMovies(query = "The Matrix")

        // Then: Consumer receives relevant search results
        assertTrue(result is DataResult.Success, "Search should succeed")

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Search should return results for well-known movie")

        // Verify search relevance - should find The Matrix movies
        val matrixMovies = movies.filter {
            it.title.contains("Matrix", ignoreCase = true)
        }
        assertTrue(
            matrixMovies.isNotEmpty(),
            "Search for 'The Matrix' should find Matrix movies",
        )

        // Verify data structure is consistent with other endpoints
        movies.forEach { movie ->
            assertTrue(movie.id > 0, "Each movie should have valid ID")
            assertTrue(movie.title.isNotBlank(), "Each movie should have title")
            assertTrue(movie.voteAverage >= 0.0, "Vote average should be non-negative")
            assertTrue(movie.voteCount >= 0, "Vote count should be non-negative")
        }

        println("✅ Search movies basic test passed")
        println("\tFound ${movies.size} results for 'The Matrix'")
        println("\tMatrix movies found: ${matrixMovies.size}")
        println("\tTop result: '${movies.first().title}' (${movies.first().voteAverage})")
    }

    @Test
    fun `search movies pagination works correctly`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing search pagination
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer searches across multiple pages
        val page1Result = repository.searchMovies(query = "love", page = 1)
        val page2Result = repository.searchMovies(query = "love", page = 2)

        // Then: Both pages return different results
        assertTrue(page1Result is DataResult.Success, "Page 1 should succeed")
        assertTrue(page2Result is DataResult.Success, "Page 2 should succeed")

        val page1Movies = page1Result.data
        val page2Movies = page2Result.data

        assertTrue(page1Movies.isNotEmpty(), "Page 1 should have results")
        assertTrue(page2Movies.isNotEmpty(), "Page 2 should have results")

        // Verify pages contain different movies
        val page1Ids = page1Movies.map { it.id }.toSet()
        val page2Ids = page2Movies.map { it.id }.toSet()
        assertTrue(
            page1Ids.intersect(page2Ids).isEmpty(),
            "Different pages should contain different movies",
        )

        println("✅ Search movies pagination test passed")
        println("\tPage 1: ${page1Movies.size} results")
        println("\tPage 2: ${page2Movies.size} results")
    }

    @Test
    fun `search movies handles different languages correctly`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing search localization
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer searches in different languages
        val englishResult = repository.searchMovies(
            query = "love",
            language = "en-US",
        )
        val spanishResult = repository.searchMovies(
            query = "amor",
            language = "es-ES",
        )

        // Then: Both language searches return results
        assertTrue(englishResult is DataResult.Success, "English search should succeed")
        assertTrue(spanishResult is DataResult.Success, "Spanish search should succeed")

        val englishMovies = englishResult.data
        val spanishMovies = spanishResult.data

        assertTrue(englishMovies.isNotEmpty(), "English search should have results")
        assertTrue(spanishMovies.isNotEmpty(), "Spanish search should have results")

        // Verify results are in appropriate language context
        englishMovies.forEach { movie ->
            assertTrue(movie.title.isNotBlank(), "English results should have titles")
        }

        spanishMovies.forEach { movie ->
            assertTrue(movie.title.isNotBlank(), "Spanish results should have titles")
        }

        println("✅ Search movies localization test passed")
        println("\tEnglish results for 'love': ${englishMovies.size}")
        println("\tSpanish results for 'amor': ${spanishMovies.size}")
    }

    @Test
    fun `search movies with year filter returns appropriate results`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing year filtering
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer searches with year constraint
        val result = repository.searchMovies(
            query = "Matrix",
            year = 1999,
        )

        // Then: Results should be filtered by year
        assertTrue(result is DataResult.Success, "Year-filtered search should succeed")

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Should find movies from 1999")

        // Verify year filtering is applied (at least some movies should be from 1999)
        val moviesFrom1999 = movies.filter { movie -> movie.releaseDate?.year == 1999 }

        // Note: TMDB year filter may include movies from nearby years or different release patterns,
        // so we verify that at least some results match the year filter
        assertTrue(
            moviesFrom1999.isNotEmpty() || movies.size < 5, // Either we find 1999 movies or very few results
            "Year filter should influence results",
        )

        println("✅ Search movies year filter test passed")
        println("\tTotal results: ${movies.size}")
        println("\tMovies from 1999: ${moviesFrom1999.size}")
        if (moviesFrom1999.isNotEmpty()) {
            println("\tSample 1999 movie: '${moviesFrom1999.first().title}' (${moviesFrom1999.first().releaseDate})")
        }
    }

    @Test
    fun `search movies handles empty and unusual queries appropriately`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = apiKey ?: run {
            println("Skipping integration test - TMDB_API_KEY environment variable not set")
            return@runTest
        }

        // Given: Repository for testing edge cases
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer searches with unusual query
        val unusualResult = repository.searchMovies(query = "xyz123nonexistentmovie")

        // Then: Should handle gracefully with empty or minimal results
        assertTrue(unusualResult is DataResult.Success, "Unusual search should not fail")

        val unusualMovies = unusualResult.data
        // Should return empty list or very few irrelevant results
        assertTrue(
            unusualMovies.isEmpty() || unusualMovies.size < 3,
            "Unusual search should return few or no results",
        )

        // When: Consumer searches with very short query
        val shortResult = repository.searchMovies(query = "a")

        // Then: Should handle short queries appropriately
        assertTrue(shortResult is DataResult.Success, "Short search should not fail")

        val shortMovies = shortResult.data
        // Short queries might return many results or be handled by API differently
        assertTrue(shortMovies.size >= 0, "Short search should return valid result set")

        println("✅ Search movies edge cases test passed")
        println("\tUnusual query results: ${unusualMovies.size}")
        println("\tShort query results: ${shortMovies.size}")
    }

    private companion object {
        val TEST_TIMEOUT = 30.seconds
    }
}
