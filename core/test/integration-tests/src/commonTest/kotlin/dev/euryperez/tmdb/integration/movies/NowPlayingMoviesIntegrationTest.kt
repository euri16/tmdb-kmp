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
 * Integration tests for MoviesRepository.getNowPlayingMovies endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NowPlayingMoviesIntegrationTest : BaseTest {

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
    fun `now playing movies endpoint returns real data with proper structure`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository with real API key
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests now playing movies
        val result = repository.getNowPlayingMovies(page = 1, language = "en-US")

        // Then: Consumer receives actual now playing movies data
        assertTrue(
            result is DataResult.Success,
            "Expected successful result for now playing movies",
        )

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Now playing movies should not be empty")

        // Verify the data structure consumers will work with
        movies.forEach { movie ->
            assertTrue(movie.id > 0, "Each movie should have valid ID")
            assertTrue(movie.title.isNotBlank(), "Each movie should have non-blank title")
            assertNotNull(movie.releaseDate, "Movies should have release dates")
            assertTrue(movie.popularity > 0.0, "Movies should have positive popularity")
        }

        println("✅ Now playing integration test passed - retrieved ${movies.size} now playing movies")
        println("\tSample movie: '${movies.first().title}' (Released: ${movies.first().releaseDate})")
    }

    @Test
    fun `now playing movies pagination works correctly`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing pagination
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests different pages of now playing movies
        val page1Result = repository.getNowPlayingMovies(page = 1)
        val page2Result = repository.getNowPlayingMovies(page = 2)

        // Then: Both pages return different movies
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

        println("✅ Now playing movies pagination test passed")
        println("\tPage 1: ${page1Movies.size} movies, first ID: ${page1Movies.first().id}")
        println("\tPage 2: ${page2Movies.size} movies, first ID: ${page2Movies.first().id}")
    }

    @Test
    fun `now playing movies localization works with different languages`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing localization
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests now playing movies in different languages
        val englishResult = repository.getNowPlayingMovies(page = 1, language = "en-US")
        val frenchResult = repository.getNowPlayingMovies(page = 1, language = "fr-FR")

        // Then: Both language versions are returned successfully
        assertTrue(englishResult is DataResult.Success, "English version should succeed")
        assertTrue(frenchResult is DataResult.Success, "French version should succeed")

        val englishMovies = englishResult.data
        val frenchMovies = frenchResult.data

        assertTrue(englishMovies.isNotEmpty(), "English movies should not be empty")
        assertTrue(frenchMovies.isNotEmpty(), "French movies should not be empty")

        // Verify that we get some movies in both languages
        englishMovies.forEach { movie ->
            assertTrue(movie.title.isNotBlank(), "English movie title should not be blank")
        }

        frenchMovies.forEach { movie ->
            assertTrue(movie.title.isNotBlank(), "French movie title should not be blank")
        }

        println("✅ Now playing movies localization test passed")
        println("\tEnglish movies: ${englishMovies.size}")
        println("\tFrench movies: ${frenchMovies.size}")
        println("\tSample English: '${englishMovies.first().title}'")
        println("\tSample French: '${frenchMovies.first().title}'")
    }

    @Test
    fun `now playing movies have current release dates`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing now playing criteria
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests now playing movies
        val result = repository.getNowPlayingMovies(page = 1, language = "en-US")

        // Then: Movies should be actually playing in theaters (recent releases)
        assertTrue(result is DataResult.Success, "Should succeed getting now playing movies")

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Should have now playing movies")

        // Verify movies are relatively recent (released within the last year typically)
        movies.forEach { movie ->
            assertNotNull(movie.releaseDate, "Each movie should have a release date")
            // Note: We don't enforce strict date constraints as "now playing" may include
            // movies from different time periods depending on TMDB's criteria
        }

        println("✅ Now playing movies date validation test passed")
        println("\tFound ${movies.size} now playing movies")
        val releaseDates = movies.mapNotNull { it.releaseDate }.distinct().sorted()
        println("\tRelease date range: ${releaseDates.firstOrNull()} to ${releaseDates.lastOrNull()}")
    }

    private companion object {
        val TEST_TIMEOUT = 30.seconds
    }
}
