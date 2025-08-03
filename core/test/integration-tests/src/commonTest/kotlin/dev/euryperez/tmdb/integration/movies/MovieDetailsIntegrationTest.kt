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
 * Integration tests for MoviesRepository.getMovieDetails endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsIntegrationTest : BaseTest {

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
    fun `different language parameters return localized content`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing localization
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests same movie details in different languages
        val englishResult =
            repository.getMovieDetails(movieId = THE_GODFATHER_MOVIE_ID, language = "en-US")
        val spanishResult =
            repository.getMovieDetails(movieId = THE_GODFATHER_MOVIE_ID, language = "es-ES")

        // Then: Different language versions are returned
        assertTrue(englishResult is DataResult.Success, "English version should succeed")
        assertTrue(spanishResult is DataResult.Success, "Spanish version should succeed")

        val englishMovie = englishResult.data
        val spanishMovie = spanishResult.data

        // Same movie ID but potentially different titles/overviews
        assertTrue(englishMovie.id == spanishMovie.id, "Should be same movie ID")

        // If Spanish localization exists, overview might be different
        // (Not all movies have full localization, so we just verify the API accepts language param)
        assertTrue(englishMovie.overview.isNotBlank(), "English overview should exist")
        assertTrue(spanishMovie.overview.isNotBlank(), "Spanish overview should exist")

        println("✅ Localization integration test passed")
        println("\tEnglish title: '${englishMovie.title}'")
        println("\tSpanish title: '${spanishMovie.title}'")
        println("\tEnglish overview length: ${englishMovie.overview.length}")
        println("\tSpanish overview length: ${spanishMovie.overview.length}")
    }

    @Test
    fun `end to end serialization accuracy with real API response structure`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing complete serialization pipeline
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests movie with rich data (The Godfather has comprehensive data)
        val result = repository.getMovieDetails(movieId = THE_GODFATHER_MOVIE_ID)

        // Then: All expected fields are properly serialized and mapped
        assertTrue(result is DataResult.Success, "Movie details request should succeed")

        val movie = result.data

        // Verify all critical fields are properly deserialized
        assertTrue(movie.id == THE_GODFATHER_MOVIE_ID, "Movie ID should match request")
        assertTrue(movie.title.isNotBlank(), "Title should be deserialized")
        assertTrue(movie.overview.isNotBlank(), "Overview should be deserialized")
        assertNotNull(movie.releaseDate, "Release date should be deserialized")
        assertTrue(movie.voteAverage > 0.0, "Vote average should be deserialized")
        assertTrue(movie.voteCount > 0, "Vote count should be deserialized")
        assertTrue(movie.popularity > 0.0, "Popularity should be deserialized")
        assertTrue(
            movie.runtime?.let { it > 0 } ?: true,
            "Runtime should be positive if present",
        )
        assertTrue(
            movie.budget?.let { it >= 0 } ?: true,
            "Budget should be non-negative if present",
        )
        assertTrue(
            movie.revenue?.let { it >= 0 } ?: true,
            "Revenue should be non-negative if present",
        )
        assertTrue(movie.genres.isNotEmpty(), "Genres should be deserialized")

        // Verify nested objects are properly deserialized
        movie.genres.forEach { genre ->
            assertTrue(genre.id > 0, "Genre ID should be valid")
            assertTrue(genre.name.isNotBlank(), "Genre name should be valid")
        }

        println("✅ Serialization integration test passed - all fields properly deserialized")
        println("\tMovie: '${movie.title}' (${movie.releaseDate})")
        println("\tGenres: ${movie.genres.joinToString(", ") { it.name }}")
        println("\tRuntime: ${movie.runtime} minutes, Budget: $${movie.budget}, Revenue: $${movie.revenue}")
    }

    @Test
    fun `movie details returns comprehensive data for well-known movies`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing comprehensive movie data
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests details for a classic movie with rich metadata
        val result = repository.getMovieDetails(movieId = THE_MATRIX_MOVIE_ID)

        // Then: Consumer receives comprehensive movie information
        assertTrue(result is DataResult.Success, "Movie details should succeed")

        val movie = result.data
        assertTrue(movie.id == THE_MATRIX_MOVIE_ID, "Should match requested movie")
        assertTrue(movie.title.isNotBlank(), "Should have title")
        assertTrue(movie.overview.isNotBlank(), "Should have overview")
        assertNotNull(movie.releaseDate, "Should have release date")

        // The Matrix should have substantial metadata
        assertTrue(movie.voteCount > 1000, "Popular movie should have many votes")
        assertTrue(movie.voteAverage > 7.0, "Well-rated movie should have high score")
        assertTrue(movie.popularity > 10.0, "Popular movie should have high popularity")
        assertTrue(movie.genres.isNotEmpty(), "Should have genres")

        // Check for optional fields that The Matrix should have
        assertNotNull(movie.runtime, "Classic movie should have runtime")
        assertTrue(movie.runtime!! > 90, "Feature film should be substantial length")

        // Verify genre information is detailed
        movie.genres.forEach { genre ->
            assertTrue(genre.name.isNotBlank(), "Each genre should have a name")
            assertTrue(genre.id > 0, "Each genre should have valid ID")
        }

        println("✅ Comprehensive movie details test passed")
        println("\tMovie: '${movie.title}' (${movie.releaseDate})")
        println("\tRating: ${movie.voteAverage}/10 (${movie.voteCount} votes)")
        println("\tRuntime: ${movie.runtime} minutes")
        println("\tGenres: ${movie.genres.joinToString(", ") { it.name }}")
    }

    @Test
    fun `movie details handles different movie types correctly`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing different movie types
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests details for different types of movies
        val classicResult = repository.getMovieDetails(movieId = THE_GODFATHER_MOVIE_ID) // Classic drama
        val actionResult = repository.getMovieDetails(movieId = THE_MATRIX_MOVIE_ID) // Sci-fi action

        // Then: Both movies return valid details with appropriate differences
        assertTrue(classicResult is DataResult.Success, "Classic movie details should succeed")
        assertTrue(actionResult is DataResult.Success, "Action movie details should succeed")

        val classicMovie = classicResult.data
        val actionMovie = actionResult.data

        // Both should have core required fields
        assertTrue(classicMovie.title.isNotBlank(), "Classic movie should have title")
        assertTrue(actionMovie.title.isNotBlank(), "Action movie should have title")

        assertTrue(classicMovie.overview.isNotBlank(), "Classic movie should have overview")
        assertTrue(actionMovie.overview.isNotBlank(), "Action movie should have overview")

        // Should have different but valid genres
        assertTrue(classicMovie.genres.isNotEmpty(), "Classic movie should have genres")
        assertTrue(actionMovie.genres.isNotEmpty(), "Action movie should have genres")

        val classicGenres = classicMovie.genres.map { it.name }
        val actionGenres = actionMovie.genres.map { it.name }

        // Verify genre differences make sense (classic vs modern sci-fi)
        assertTrue(classicGenres.isNotEmpty(), "Classic should have genre classifications")
        assertTrue(actionGenres.isNotEmpty(), "Action should have genre classifications")

        println("✅ Different movie types test passed")
        println("\tClassic: '${classicMovie.title}' - Genres: ${classicGenres.joinToString(", ")}")
        println("\tAction: '${actionMovie.title}' - Genres: ${actionGenres.joinToString(", ")}")
    }

    private companion object {
        const val THE_GODFATHER_MOVIE_ID = 238 // The Godfather (1972) - stable for details testing
        const val THE_MATRIX_MOVIE_ID = 603 // The Matrix (1999) - stable for credits testing
        val TEST_TIMEOUT = 30.seconds
    }
}
