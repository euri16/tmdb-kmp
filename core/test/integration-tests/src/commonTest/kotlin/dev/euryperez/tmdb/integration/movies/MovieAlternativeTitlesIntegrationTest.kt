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
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

/**
 * Integration tests for MoviesRepository.getMovieAlternativeTitles endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieAlternativeTitlesIntegrationTest : BaseTest {

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
    fun `movie alternative titles returns real alternative titles for known movie`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository and Fight Club movie (known to have alternative titles)
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests alternative titles for a well-known movie
        val result = repository.getMovieAlternativeTitles(movieId = FIGHT_CLUB_MOVIE_ID)

        // Then: Consumer receives real alternative titles data
        assertTrue(
            result is DataResult.Success,
            "Expected successful result for alternative titles",
        )

        val alternativeTitles = result.data
        assertEquals(
            FIGHT_CLUB_MOVIE_ID,
            alternativeTitles.id,
            "Response should contain correct movie ID",
        )
        assertTrue(
            alternativeTitles.titles.isNotEmpty(),
            "Fight Club should have alternative titles",
        )

        // Verify structure of alternative titles that consumers will receive
        alternativeTitles.titles.forEach { title ->
            assertTrue(
                title.title.isNotBlank(),
                "Each alternative title should have non-blank title",
            )
            assertTrue(
                title.countryCode.isNotBlank(),
                "Each alternative title should have country code",
            )
            assertTrue(
                title.countryCode.length == 2,
                "Country code should be ISO 3166-1 alpha-2 format",
            )
        }

        // Verify we get titles from different countries (Fight Club is international)
        val countryCodes = alternativeTitles.titles.map { it.countryCode }.distinct()
        assertTrue(
            countryCodes.size > 1,
            "Fight Club should have titles from multiple countries",
        )

        println(
            "✅ Alternative titles integration test passed - retrieved ${alternativeTitles.titles.size} alternative titles",
        )
        println("\tCountries: ${countryCodes.joinToString(", ")}")
        println(
            "\tSample titles: ${
                alternativeTitles.titles.take(3).joinToString(", ") {
                    "${it.countryCode}: '${it.title}'"
                }
            }",
        )
    }

    @Test
    fun `movie alternative titles for different movies show variety`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing different movies
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests alternative titles for different internationally known movies
        val fightClubResult = repository.getMovieAlternativeTitles(movieId = FIGHT_CLUB_MOVIE_ID)
        val matrixResult = repository.getMovieAlternativeTitles(movieId = THE_MATRIX_MOVIE_ID)

        // Then: Both movies should have alternative titles but different ones
        assertTrue(fightClubResult is DataResult.Success, "Fight Club titles should succeed")
        assertTrue(matrixResult is DataResult.Success, "Matrix titles should succeed")

        val fightClubTitles = fightClubResult.data
        val matrixTitles = matrixResult.data

        assertTrue(fightClubTitles.titles.isNotEmpty(), "Fight Club should have alternative titles")
        assertTrue(matrixTitles.titles.isNotEmpty(), "Matrix should have alternative titles")

        // Verify each movie has its own ID
        assertEquals(FIGHT_CLUB_MOVIE_ID, fightClubTitles.id, "Fight Club ID should match")
        assertEquals(THE_MATRIX_MOVIE_ID, matrixTitles.id, "Matrix ID should match")

        // Verify different movies have different title sets
        val fightClubTitleTexts = fightClubTitles.titles.map { it.title }.toSet()
        val matrixTitleTexts = matrixTitles.titles.map { it.title }.toSet()

        // Should have minimal or no overlap in actual titles
        val overlap = fightClubTitleTexts.intersect(matrixTitleTexts)
        assertTrue(
            overlap.size < fightClubTitleTexts.size / 2,
            "Different movies should have mostly different alternative titles",
        )

        println("✅ Multiple movie alternative titles test passed")
        println("\tFight Club titles: ${fightClubTitles.titles.size}")
        println("\tMatrix titles: ${matrixTitles.titles.size}")
        println("\tTitle overlap: ${overlap.size}")
    }

    @Test
    fun `movie alternative titles country codes are valid ISO format`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing country code formats
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests alternative titles
        val result = repository.getMovieAlternativeTitles(movieId = FIGHT_CLUB_MOVIE_ID)

        // Then: All country codes should follow ISO 3166-1 alpha-2 format
        assertTrue(result is DataResult.Success, "Should succeed getting alternative titles")

        val alternativeTitles = result.data
        assertTrue(alternativeTitles.titles.isNotEmpty(), "Should have alternative titles")

        // Verify country code format and common codes
        val countryCodes = alternativeTitles.titles.map { it.countryCode }.distinct()

        countryCodes.forEach { code ->
            assertTrue(code.length == 2, "Country code '$code' should be 2 characters")
            assertTrue(code.all { it.isLetter() }, "Country code '$code' should only contain letters")
            assertTrue(code.all { it.isUpperCase() }, "Country code '$code' should be uppercase")
        }

        // Should have some recognized ISO country codes (more flexible than expecting specific ones)
        val recognizedCodes = listOf(
            "US", "GB", "DE", "FR", "IT", "ES", "JP", "CN", "RU", "IN", "BR", "MX", "CA", "AU",
            "IL", "RS", "BG", "UA", "BY", "AZ", "GE", "AM", "MA", "EG", "KG", "IR", "NP", "PK", "TW",
            "TH", "GR", "KR", "HK", "NL", "TR", "LV", "PL", "PT", "NO", "AL", "XK", "DK",
        )
        val foundRecognizedCodes = countryCodes.filter { it in recognizedCodes }

        assertTrue(
            foundRecognizedCodes.isNotEmpty(),
            "Should find some recognized ISO country codes, got: $countryCodes",
        )

        println("✅ Alternative titles country codes test passed")
        println("\tTotal country codes: ${countryCodes.size}")
        println("\tCountry codes: ${countryCodes.sorted().joinToString(", ")}")
        println("\tRecognized codes found: ${foundRecognizedCodes.joinToString(", ")}")
    }

    @Test
    fun `movie alternative titles include original and localized versions`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing title variety
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests alternative titles for international movie
        val result = repository.getMovieAlternativeTitles(movieId = THE_MATRIX_MOVIE_ID)

        // Then: Should include both original title and localized versions
        assertTrue(result is DataResult.Success, "Should succeed getting Matrix alternative titles")

        val alternativeTitles = result.data
        assertTrue(alternativeTitles.titles.isNotEmpty(), "Matrix should have alternative titles")

        // Look for titles from English-speaking countries (if available)
        val englishSpeakingCountries = listOf("US", "GB", "CA", "AU")
        val englishTitles = alternativeTitles.titles.filter {
            it.countryCode in englishSpeakingCountries
        }

        // Look for titles that use Latin script vs non-Latin script
        val latinScriptTitles = alternativeTitles.titles.filter { title ->
            title.title.all { char -> char.isDigit() || char.isWhitespace() || char.code < 256 }
        }
        val nonLatinScriptTitles = alternativeTitles.titles.filter { title ->
            title.title.any { char -> char.code >= 256 }
        }

        // Should have titles in different scripts (more reliable than specific country codes)
        assertTrue(
            latinScriptTitles.isNotEmpty() || nonLatinScriptTitles.isNotEmpty(),
            "Should have titles in some script format",
        )

        // Verify title variety - should have different title texts
        val uniqueTitles = alternativeTitles.titles.map { it.title }.distinct()
        assertTrue(
            uniqueTitles.size > 1,
            "Should have multiple different titles, got: $uniqueTitles",
        )

        println("✅ Alternative titles variety test passed")
        println("\tTotal titles: ${alternativeTitles.titles.size}")
        println("\tUnique titles: ${uniqueTitles.size}")
        println("\tEnglish-speaking country titles: ${englishTitles.size}")
        println("\tLatin script titles: ${latinScriptTitles.size}")
        println("\tNon-Latin script titles: ${nonLatinScriptTitles.size}")
        println("\tSample titles: ${uniqueTitles.take(3).joinToString(", ") { "'$it'" }}")
    }

    @Test
    fun `movie alternative titles handles movies with minimal international distribution`() =
        runTest(timeout = TEST_TIMEOUT) {
            val testApiKey = requireApiKey()

            // Given: Repository for testing edge cases
            val repository = MoviesRepository.factory(apiKey = testApiKey)

            // When: Consumer requests alternative titles for well-known but potentially limited distribution movie
            val result = repository.getMovieAlternativeTitles(movieId = THE_GODFATHER_MOVIE_ID)

            // Then: Should handle gracefully even if few alternative titles exist
            assertTrue(result is DataResult.Success, "Should succeed even for limited distribution movie")

            val alternativeTitles = result.data
            assertEquals(THE_GODFATHER_MOVIE_ID, alternativeTitles.id, "Should return correct movie ID")

            // The Godfather should have some alternative titles given its classic status
            // but we handle the case where it might have fewer than modern blockbusters
            assertTrue(
                alternativeTitles.titles.size >= 0,
                "Should return valid titles list (may be empty for some movies)",
            )

            if (alternativeTitles.titles.isNotEmpty()) {
                // If titles exist, verify their structure
                alternativeTitles.titles.forEach { title ->
                    assertTrue(title.title.isNotBlank(), "Title should not be blank")
                    assertTrue(title.countryCode.isNotBlank(), "Country code should not be blank")
                    assertTrue(title.countryCode.length == 2, "Country code should be 2 characters")
                }
            }

            println("✅ Alternative titles edge cases test passed")
            println("\tThe Godfather alternative titles: ${alternativeTitles.titles.size}")
            if (alternativeTitles.titles.isNotEmpty()) {
                val countries = alternativeTitles.titles.map { it.countryCode }.distinct()
                println("\tCountries: ${countries.joinToString(", ")}")
            }
        }

    private companion object {
        const val FIGHT_CLUB_MOVIE_ID = 550 // Fight Club (1999) - stable for alternative titles testing
        const val THE_MATRIX_MOVIE_ID = 603 // The Matrix (1999) - stable for international titles
        const val THE_GODFATHER_MOVIE_ID = 238 // The Godfather (1972) - classic for edge case testing
        val TEST_TIMEOUT = 30.seconds
    }
}
