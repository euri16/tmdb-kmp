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
 * Integration tests for MoviesRepository.getMovieCredits endpoint that test real API calls to TMDB.
 * These tests verify end-to-end functionality from consumer perspective.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MovieCreditsIntegrationTest : BaseTest {

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
    fun `movie credits returns proper cast and crew data structure`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository configured for testing movie credits endpoint
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests credits for The Matrix (known to have extensive cast/crew)
        val result = repository.getMovieCredits(movieId = THE_MATRIX_MOVIE_ID)

        // Then: Consumer receives proper credits data structure
        assertTrue(result is DataResult.Success, "Expected successful result for movie credits")

        val credits = result.data
        assertEquals(THE_MATRIX_MOVIE_ID, credits.id, "Credits should match requested movie ID")
        assertTrue(credits.cast.isNotEmpty(), "Movie should have cast members")
        assertTrue(credits.crew.isNotEmpty(), "Movie should have crew members")

        // Verify cast member structure (actors like Keanu Reeves)
        val castMember = credits.cast.first()
        assertNotNull(castMember.name, "Cast member should have name")
        assertNotNull(castMember.character, "Cast member should have character name")
        assertNotNull(castMember.castId, "Cast member should have cast ID")
        assertNotNull(castMember.order, "Cast member should have order")
        assertTrue(castMember.department == null, "Cast member should not have department")
        assertTrue(castMember.job == null, "Cast member should not have job")

        // Verify crew member structure (directors like Wachowski sisters)
        val crewMember = credits.crew.first()
        assertNotNull(crewMember.name, "Crew member should have name")
        assertNotNull(crewMember.department, "Crew member should have department")
        assertNotNull(crewMember.job, "Crew member should have job")
        assertTrue(crewMember.character == null, "Crew member should not have character")
        assertTrue(crewMember.castId == null, "Crew member should not have cast ID")
        assertTrue(crewMember.order == null, "Crew member should not have order")

        // Verify we have expected main cast members
        val keanuReeves = credits.cast.find { it.name.contains("Keanu Reeves", ignoreCase = true) }
        assertNotNull(keanuReeves, "Should find Keanu Reeves in cast")
        assertTrue(
            keanuReeves.character?.contains("Neo", ignoreCase = true) == true,
            "Keanu Reeves should play Neo",
        )

        // Verify we have expected crew members (directors)
        val directors = credits.crew.filter { it.job?.contains("Director", ignoreCase = true) == true }
        assertTrue(directors.isNotEmpty(), "Should have directors in crew")

        println("✅ Movie credits integration test passed")
        println("\tFound ${credits.cast.size} cast members and ${credits.crew.size} crew members")
        println("\tMain actor: ${keanuReeves?.name} as ${keanuReeves?.character}")
        println("\tDirectors: ${directors.joinToString(", ") { "${it.name} (${it.job})" }}")
    }

    @Test
    fun `movie credits cast ordering is consistent`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing cast ordering
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests credits for a movie with well-defined cast
        val result = repository.getMovieCredits(movieId = THE_GODFATHER_MOVIE_ID)

        // Then: Cast members should be properly ordered
        assertTrue(result is DataResult.Success, "Credits request should succeed")

        val credits = result.data
        assertTrue(credits.cast.isNotEmpty(), "Should have cast members")

        // Verify cast ordering is sequential
        val castOrders = credits.cast.mapNotNull { it.order }.sorted()
        assertTrue(castOrders.isNotEmpty(), "Cast members should have order values")

        // Check that orders start from 0 and are mostly sequential
        assertTrue(castOrders.first() >= 0, "Cast order should start from 0 or positive number")

        // Main actors should be in early positions
        val leadActors = credits.cast.filter { (it.order ?: Int.MAX_VALUE) < 5 }
        assertTrue(leadActors.size >= 3, "Should have several lead actors in top positions")

        // Verify lead actors have character names
        leadActors.forEach { actor ->
            assertTrue(
                actor.character?.isNotBlank() == true,
                "Lead actors should have character names: ${actor.name}",
            )
        }

        println("✅ Movie credits cast ordering test passed")
        println("\tCast size: ${credits.cast.size}")
        println("\tOrder range: ${castOrders.first()} to ${castOrders.last()}")
        println("\tLead actors: ${leadActors.joinToString(", ") { "${it.name} (${it.character})" }}")
    }

    @Test
    fun `movie credits crew departments are properly categorized`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing crew department structure
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests credits for a major production
        val result = repository.getMovieCredits(movieId = THE_MATRIX_MOVIE_ID)

        // Then: Crew should be organized by departments with clear job roles
        assertTrue(result is DataResult.Success, "Credits request should succeed")

        val credits = result.data
        assertTrue(credits.crew.isNotEmpty(), "Should have crew members")

        // Group crew by department
        val crewByDepartment = credits.crew.groupBy { it.department }
        assertTrue(crewByDepartment.isNotEmpty(), "Should have crew departments")

        // Verify common departments exist for a major film
        val expectedDepartments = listOf("Directing", "Production", "Camera", "Editing")
        val actualDepartments = crewByDepartment.keys.filterNotNull()

        val foundExpectedDepts = expectedDepartments.filter { expected ->
            actualDepartments.any { actual -> actual.contains(expected, ignoreCase = true) }
        }
        assertTrue(
            foundExpectedDepts.size >= 2,
            "Should find at least 2 common departments, found: $foundExpectedDepts in $actualDepartments",
        )

        // Verify each department has proper job roles
        crewByDepartment.forEach { (department, members) ->
            if (department != null) {
                members.forEach { member ->
                    assertNotNull(member.job, "Crew member in $department should have job title")
                    assertTrue(member.job!!.isNotBlank(), "Job title should not be blank")
                }
            }
        }

        // Find directors specifically
        val directors = credits.crew.filter {
            it.job?.equals("Director", ignoreCase = true) == true
        }
        assertTrue(directors.isNotEmpty(), "Should have at least one director")

        println("✅ Movie credits crew departments test passed")
        println("\tTotal crew: ${credits.crew.size}")
        println("\tDepartments: ${actualDepartments.joinToString(", ")}")
        println("\tDirectors: ${directors.joinToString(", ") { it.name }}")
    }

    @Test
    fun `movie credits localization affects person names appropriately`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing credits localization
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests credits in different languages
        val englishResult = repository.getMovieCredits(
            movieId = THE_MATRIX_MOVIE_ID,
            language = "en-US",
        )
        val frenchResult = repository.getMovieCredits(
            movieId = THE_MATRIX_MOVIE_ID,
            language = "fr-FR",
        )

        // Then: Both should return valid credits with appropriate localization
        assertTrue(englishResult is DataResult.Success, "English credits should succeed")
        assertTrue(frenchResult is DataResult.Success, "French credits should succeed")

        val englishCredits = englishResult.data
        val frenchCredits = frenchResult.data

        // Same movie, same cast/crew counts
        assertEquals(englishCredits.id, frenchCredits.id, "Should be same movie")
        assertTrue(englishCredits.cast.isNotEmpty(), "English should have cast")
        assertTrue(frenchCredits.cast.isNotEmpty(), "French should have cast")

        // Person names should generally be the same (names don't typically translate)
        val englishActorNames = englishCredits.cast.take(5).map { it.name }
        val frenchActorNames = frenchCredits.cast.take(5).map { it.name }

        // At least some names should be identical (actor names are proper nouns)
        val identicalNames = englishActorNames.intersect(frenchActorNames.toSet())
        assertTrue(
            identicalNames.isNotEmpty(),
            "Some actor names should be identical across languages",
        )

        println("✅ Movie credits localization test passed")
        println("\tEnglish cast size: ${englishCredits.cast.size}")
        println("\tFrench cast size: ${frenchCredits.cast.size}")
        println("\tIdentical names: ${identicalNames.size}/${englishActorNames.size}")
    }

    @Test
    fun `movie credits handles movies with minimal crew information`() = runTest(timeout = TEST_TIMEOUT) {
        val testApiKey = requireApiKey()

        // Given: Repository for testing various movie credit scenarios
        val repository = MoviesRepository.factory(apiKey = testApiKey)

        // When: Consumer requests credits for different movie types
        val blockbusterResult = repository.getMovieCredits(movieId = THE_MATRIX_MOVIE_ID)
        val classicResult = repository.getMovieCredits(movieId = THE_GODFATHER_MOVIE_ID)

        // Then: Both should have credits but potentially different levels of detail
        assertTrue(blockbusterResult is DataResult.Success, "Blockbuster credits should succeed")
        assertTrue(classicResult is DataResult.Success, "Classic movie credits should succeed")

        val blockbusterCredits = blockbusterResult.data
        val classicCredits = classicResult.data

        // Both should have core cast and crew
        assertTrue(blockbusterCredits.cast.isNotEmpty(), "Blockbuster should have cast")
        assertTrue(blockbusterCredits.crew.isNotEmpty(), "Blockbuster should have crew")
        assertTrue(classicCredits.cast.isNotEmpty(), "Classic should have cast")
        assertTrue(classicCredits.crew.isNotEmpty(), "Classic should have crew")

        // Verify essential crew roles exist
        val blockbusterDirectors = blockbusterCredits.crew.filter {
            it.job?.contains("Director", ignoreCase = true) == true
        }
        val classicDirectors = classicCredits.crew.filter {
            it.job?.contains("Director", ignoreCase = true) == true
        }

        assertTrue(blockbusterDirectors.isNotEmpty(), "Blockbuster should have directors")
        assertTrue(classicDirectors.isNotEmpty(), "Classic should have directors")

        println("✅ Movie credits variation test passed")
        println("\tBlockbuster - Cast: ${blockbusterCredits.cast.size}, Crew: ${blockbusterCredits.crew.size}")
        println("\tClassic - Cast: ${classicCredits.cast.size}, Crew: ${classicCredits.crew.size}")
    }

    private companion object {
        const val THE_MATRIX_MOVIE_ID = 603 // The Matrix (1999) - stable for credits testing
        const val THE_GODFATHER_MOVIE_ID = 238 // The Godfather (1972) - stable for comparison
        val TEST_TIMEOUT = 30.seconds
    }
}
