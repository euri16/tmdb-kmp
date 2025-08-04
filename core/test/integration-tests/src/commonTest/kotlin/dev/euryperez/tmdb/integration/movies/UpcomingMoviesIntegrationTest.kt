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
 * Integration tests for MoviesRepository.getUpcomingMovies endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UpcomingMoviesIntegrationTest : BaseTest {

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
    fun `upcoming movies endpoint returns real upcoming movie data`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository with real API key
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests upcoming movies
        val result = repository.getUpcomingMovies(page = 1, language = "en-US")

        // Then: Consumer receives actual upcoming movies data
        assertTrue(
            result is DataResult.Success,
            "Expected successful result for upcoming movies",
        )

        val movies = result.data
        assertTrue(movies.isNotEmpty(), "Upcoming movies should not be empty")

        // Verify the data structure consumers will work with
        movies.forEach { movie ->
            assertTrue(movie.id > 0, "Each movie should have valid ID")
            assertTrue(movie.title.isNotBlank(), "Each movie should have non-blank title")
            assertNotNull(movie.releaseDate, "Movies should have release dates")
            assertTrue(movie.popularity > 0.0, "Movies should have positive popularity")
        }

        println("✅ Upcoming movies integration test passed - retrieved ${movies.size} upcoming movies")
        println("\tSample movie: '${movies.first().title}' (Released: ${movies.first().releaseDate})")
    }

    @Test
    fun `upcoming movies pagination works correctly`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing pagination
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests different pages of upcoming movies
        val page1Result = repository.getUpcomingMovies(page = 1)
        val page2Result = repository.getUpcomingMovies(page = 2)

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

        println("✅ Upcoming movies pagination test passed")
        println("\tPage 1: ${page1Movies.size} movies, first ID: ${page1Movies.first().id}")
        println("\tPage 2: ${page2Movies.size} movies, first ID: ${page2Movies.first().id}")
    }

    @Test
    fun `upcoming movies localization works with different languages`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing localization
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests upcoming movies in different languages
        val englishResult = repository.getUpcomingMovies(page = 1, language = "en-US")
        val spanishResult = repository.getUpcomingMovies(page = 1, language = "es-ES")

        // Then: Both language versions are returned successfully
        assertTrue(englishResult is DataResult.Success, "English version should succeed")
        assertTrue(spanishResult is DataResult.Success, "Spanish version should succeed")

        val englishMovies = englishResult.data
        val spanishMovies = spanishResult.data

        assertTrue(englishMovies.isNotEmpty(), "English movies should not be empty")
        assertTrue(spanishMovies.isNotEmpty(), "Spanish movies should not be empty")

        // Verify that we get some movies in both languages
        englishMovies.forEach { movie ->
            assertTrue(movie.title.isNotBlank(), "English movie title should not be blank")
        }

        spanishMovies.forEach { movie ->
            assertTrue(movie.title.isNotBlank(), "Spanish movie title should not be blank")
        }

        println("✅ Upcoming movies localization test passed")
        println("\tEnglish movies: ${englishMovies.size}")
        println("\tSpanish movies: ${spanishMovies.size}")
        println("\tSample English: '${englishMovies.first().title}'")
        println("\tSample Spanish: '${spanishMovies.first().title}'")
    }

    private companion object {
        val TEST_TIMEOUT = 30.seconds
    }
}
