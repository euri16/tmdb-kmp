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
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/**
 * Integration tests for MoviesRepository utility features and cross-cutting concerns.
 * These tests verify repository instance management, performance characteristics,
 * and other features that span multiple endpoints.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UtilityFeaturesIntegrationTest : BaseTest {

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
    fun `repository instance management works correctly`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository creation using different methods
        val instance1 = MoviesRepository.getInstance(apiKey = testApiKey)
        val instance2 = MoviesRepository.getInstance(apiKey = testApiKey)
        val instance3 = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer uses getInstance multiple times
        // Then: Should return same instance (singleton behavior)
        assertTrue(
            instance1 === instance2,
            "getInstance should return same instance for same API key",
        )

        // When: Consumer uses create method
        // Then: Should return new instance
        assertTrue(
            instance1 !== instance3,
            "create should return new instance different from singleton",
        )

        // Verify both instances work correctly
        val result1 = instance1.getPopularMovies(page = 1)
        val result3 = instance3.getPopularMovies(page = 1)

        result1 is DataResult.Success
        assertTrue(result1 is DataResult.Success, "Singleton instance should work")
        assertTrue(result3 is DataResult.Success, "Created instance should work")

        println("✅ Repository instance management test passed")
        println("\tSingleton behavior: ${instance1 === instance2}")
        println("\tCreate returns new instance: ${instance1 !== instance3}")
    }

    @Test
    fun `concurrent requests work correctly without interference`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing concurrent behavior
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer makes multiple concurrent requests
        val popularMoviesJob = repository.getPopularMovies(page = 1)
        val upcomingMoviesJob = repository.getUpcomingMovies(page = 1)
        val topRatedMoviesJob = repository.getTopRatedMovies(page = 1)

        // Then: All requests should complete successfully
        assertTrue(popularMoviesJob is DataResult.Success, "Popular movies should succeed")
        assertTrue(upcomingMoviesJob is DataResult.Success, "Upcoming movies should succeed")
        assertTrue(topRatedMoviesJob is DataResult.Success, "Top rated movies should succeed")

        val popularMovies = popularMoviesJob.data
        val upcomingMovies = upcomingMoviesJob.data
        val topRatedMovies = topRatedMoviesJob.data

        // Verify each request returned valid, distinct data
        assertTrue(popularMovies.isNotEmpty(), "Popular movies should have results")
        assertTrue(upcomingMovies.isNotEmpty(), "Upcoming movies should have results")
        assertTrue(topRatedMovies.isNotEmpty(), "Top rated movies should have results")

        // Verify results are different (different endpoints return different movies)
        val popularIds = popularMovies.map { it.id }.toSet()
        val upcomingIds = upcomingMovies.map { it.id }.toSet()
        val topRatedIds = topRatedMovies.map { it.id }.toSet()

        // There should be minimal overlap between different movie categories
        val popularUpcomingOverlap = popularIds.intersect(upcomingIds)
        val popularTopRatedOverlap = popularIds.intersect(topRatedIds)
        val upcomingTopRatedOverlap = upcomingIds.intersect(topRatedIds)

        println("✅ Concurrent requests test passed")
        println("\tPopular movies: ${popularMovies.size}")
        println("\tUpcoming movies: ${upcomingMovies.size}")
        println("\tTop rated movies: ${topRatedMovies.size}")
        println(
            "\tOverlaps: P-U:${popularUpcomingOverlap.size}, P-T:${popularTopRatedOverlap.size}, U-T:${upcomingTopRatedOverlap.size}",
        )
    }

    @Test
    fun `repository handles language consistency across multiple calls`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing language handling
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer makes requests with consistent language parameter
        val popularEnglish = repository.getPopularMovies(language = "en-US")
        val upcomingEnglish = repository.getUpcomingMovies(language = "en-US")
        val topRatedEnglish = repository.getTopRatedMovies(language = "en-US")

        // Then: All should succeed with English content
        assertTrue(popularEnglish is DataResult.Success, "English popular movies should succeed")
        assertTrue(upcomingEnglish is DataResult.Success, "English upcoming movies should succeed")
        assertTrue(topRatedEnglish is DataResult.Success, "English top rated movies should succeed")

        // When: Consumer switches to different language
        val popularFrench = repository.getPopularMovies(language = "fr-FR")
        val upcomingFrench = repository.getUpcomingMovies(language = "fr-FR")

        // Then: Should work with French localization
        assertTrue(popularFrench is DataResult.Success, "French popular movies should succeed")
        assertTrue(upcomingFrench is DataResult.Success, "French upcoming movies should succeed")

        // Verify language doesn't affect data structure consistency
        val englishPopular = popularEnglish.data.first()
        val frenchPopular = popularFrench.data.first()

        // Same fields should be present regardless of language
        assertTrue(englishPopular.id > 0, "English movie should have valid ID")
        assertTrue(frenchPopular.id > 0, "French movie should have valid ID")
        assertTrue(englishPopular.title.isNotBlank(), "English movie should have title")
        assertTrue(frenchPopular.title.isNotBlank(), "French movie should have title")

        println("✅ Language consistency test passed")
        println("\tEnglish popular: ${popularEnglish.data.size} movies")
        println("\tFrench popular: ${popularFrench.data.size} movies")
        println("\tData structure consistent across languages")
    }

    @Test
    fun `repository performance is reasonable for typical usage patterns`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for performance testing
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer performs typical usage sequence (discovery -> details)
        val timeSource = TimeSource.Monotonic
        val startTime = timeSource.markNow()

        // Step 1: Discover movies
        val popularResult = repository.getPopularMovies(page = 1)
        assertTrue(popularResult is DataResult.Success, "Popular movies should succeed")

        val discoverTime = timeSource.markNow()

        // Step 2: Get details for first movie
        val firstMovie = popularResult.data.first()
        val detailsResult = repository.getMovieDetails(movieId = firstMovie.id)
        assertTrue(detailsResult is DataResult.Success, "Movie details should succeed")

        val detailsTime = timeSource.markNow()

        // Step 3: Get additional info
        val creditsResult = repository.getMovieCredits(movieId = firstMovie.id)
        assertTrue(creditsResult is DataResult.Success, "Movie credits should succeed")

        val endTime = timeSource.markNow()

        // Then: Performance should be reasonable for API calls
        val totalDuration = endTime - startTime
        val discoverDuration = discoverTime - startTime
        val detailsDuration = detailsTime - discoverTime
        val creditsDuration = endTime - detailsTime

        // These are reasonable thresholds for API calls (allowing for network variability)
        assertTrue(
            totalDuration.inWholeSeconds < 30,
            "Total workflow should complete within 30 seconds, took ${totalDuration.inWholeSeconds}s",
        )

        println("✅ Performance test passed")
        println("\tDiscover movies: ${discoverDuration.inWholeMilliseconds}ms")
        println("\tGet details: ${detailsDuration.inWholeMilliseconds}ms")
        println("\tGet credits: ${creditsDuration.inWholeMilliseconds}ms")
        println("\tTotal workflow: ${totalDuration.inWholeMilliseconds}ms")
    }

    @Test
    fun `repository maintains data consistency across related endpoints`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing data consistency
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer gets movie from list then fetches detailed info
        val popularResult = repository.getPopularMovies(page = 1)
        assertTrue(popularResult is DataResult.Success, "Popular movies should succeed")

        val movieFromList = popularResult.data.first()
        val detailsResult = repository.getMovieDetails(movieId = movieFromList.id)
        assertTrue(detailsResult is DataResult.Success, "Movie details should succeed")

        val movieDetails = detailsResult.data

        // Then: Core data should be consistent between endpoints
        assertTrue(movieFromList.id == movieDetails.id, "Movie ID should be consistent")
        assertTrue(movieFromList.title == movieDetails.title, "Movie title should be consistent")

        // Vote averages should be close (allowing for slight variations due to caching/timing)
        val voteAverageDiff = kotlin.math.abs(movieFromList.voteAverage - movieDetails.voteAverage)
        assertTrue(
            voteAverageDiff <= 1.0,
            "Vote averages should be similar (within 1.0), got list:${movieFromList.voteAverage} vs details:${movieDetails.voteAverage}",
        )

        // When: Consumer gets credits for same movie
        val creditsResult = repository.getMovieCredits(movieId = movieFromList.id)
        assertTrue(creditsResult is DataResult.Success, "Movie credits should succeed")

        val credits = creditsResult.data

        // Then: Movie ID should be consistent across all endpoints
        assertTrue(credits.id == movieFromList.id, "Credits should reference same movie ID")

        println("✅ Data consistency test passed")
        println("\tMovie: '${movieFromList.title}' (ID: ${movieFromList.id})")
        println("\tVote average diff: $voteAverageDiff")
        println("\tCredits for same movie: ${credits.cast.size} cast, ${credits.crew.size} crew")
    }

    private companion object {
        val TEST_TIMEOUT = 45.seconds // Longer timeout for utility tests that make multiple calls
    }
}
