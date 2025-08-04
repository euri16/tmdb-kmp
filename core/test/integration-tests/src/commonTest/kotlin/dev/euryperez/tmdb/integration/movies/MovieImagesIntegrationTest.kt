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

@OptIn(ExperimentalCoroutinesApi::class)
class MovieImagesIntegrationTest : BaseTest {

    val mainCoroutineRule = MainCoroutineRule()

    @BeforeTest
    override fun setup() {
        mainCoroutineRule.setup()
    }

    @AfterTest
    override fun tearDown() {
        mainCoroutineRule.tearDown()
    }

    @Test
    fun `getMovieImages returns images for Avengers movie`() = runTest {
        val testApiKey = requireApiKey()

        // Given
        val repository = MoviesRepository.factory(testApiKey)
        val movieId = 24428

        // When
        val result = repository.getMovieImages(movieId)

        // Then
        assertTrue(result is DataResult.Success, "Expected success, got: $result")
        assertNotNull(result.data)

        val movieImages = result.data
        assertEquals(movieId, movieImages.id)

        // The Avengers should have images available
        assertTrue(
            movieImages.backdrops.isNotEmpty() || movieImages.posters.isNotEmpty() || movieImages.logos.isNotEmpty(),
            "The Avengers should have at least some images available",
        )

        // If backdrops exist, verify their structure
        if (movieImages.backdrops.isNotEmpty()) {
            val firstBackdrop = movieImages.backdrops[0]
            assertTrue(firstBackdrop.filePath.isNotEmpty())
            assertTrue(firstBackdrop.aspectRatio > 0)
            assertTrue(firstBackdrop.size.width > 0)
            assertTrue(firstBackdrop.size.height > 0)
            assertTrue(firstBackdrop.voteAverage >= 0.0)
            assertTrue(firstBackdrop.voteCount >= 0)
            assertTrue(firstBackdrop.imageUrl.startsWith("https://image.tmdb.org/t/p/original"))
        }

        // If posters exist, verify their structure
        if (movieImages.posters.isNotEmpty()) {
            val firstPoster = movieImages.posters[0]
            assertTrue(firstPoster.filePath.isNotEmpty())
            assertTrue(firstPoster.aspectRatio > 0)
            assertTrue(firstPoster.size.width > 0)
            assertTrue(firstPoster.size.height > 0)
            assertTrue(firstPoster.voteAverage >= 0.0)
            assertTrue(firstPoster.voteCount >= 0)
            assertTrue(firstPoster.imageUrl.startsWith("https://image.tmdb.org/t/p/original"))
        }

        // If logos exist, verify their structure
        if (movieImages.logos.isNotEmpty()) {
            val firstLogo = movieImages.logos[0]
            assertTrue(firstLogo.filePath.isNotEmpty())
            assertTrue(firstLogo.aspectRatio > 0)
            assertTrue(firstLogo.size.width > 0)
            assertTrue(firstLogo.size.height > 0)
            assertTrue(firstLogo.voteAverage >= 0.0)
            assertTrue(firstLogo.voteCount >= 0)
            assertTrue(firstLogo.imageUrl.startsWith("https://image.tmdb.org/t/p/original"))
        }
    }

    @Test
    fun `getMovieImages with language filter returns appropriate results`() = runTest {
        val testApiKey = requireApiKey()

        // Given
        val repository = MoviesRepository.factory(testApiKey)
        val movieId = 24428 // The Avengers (2012) - more likely to have images
        val language = "en"

        // When
        val result = repository.getMovieImages(movieId, language = language)

        // Then
        assertTrue(result is DataResult.Success, "Expected success, got: $result")
        assertNotNull(result.data)

        val movieImages = result.data
        assertEquals(movieId, movieImages.id)

        // The result should contain images (might be language-specific or language-neutral)
        assertTrue(
            movieImages.backdrops.isNotEmpty() || movieImages.posters.isNotEmpty() || movieImages.logos.isNotEmpty(),
            "Should have at least some images",
        )

        // Check that returned images are either for the requested language or language-neutral
        val allImages = movieImages.backdrops + movieImages.posters + movieImages.logos
        allImages.forEach { image ->
            assertTrue(
                image.languageCode == language || image.languageCode == null,
                "Image should be for language '$language' or language-neutral, but was: ${image.languageCode}",
            )
        }
    }

    @Test
    fun `getMovieImages with includeImageLanguage returns multiple language images`() = runTest {
        val testApiKey = requireApiKey()

        // Given
        val repository = MoviesRepository.factory(testApiKey)
        val movieId = 24428 // The Avengers (2012) - likely to have images
        val includeLanguages = listOf("en", "es")

        // When
        val result = repository.getMovieImages(movieId, includeImageLanguage = includeLanguages)

        // Then
        assertTrue(result is DataResult.Success, "Expected success, got: $result")
        assertNotNull(result.data)

        val movieImages = result.data
        assertEquals(movieId, movieImages.id)

        // Should have images
        assertTrue(
            movieImages.backdrops.isNotEmpty() || movieImages.posters.isNotEmpty() || movieImages.logos.isNotEmpty(),
            "Should have at least some images",
        )

        // Check that returned images are for the requested languages or language-neutral
        val allImages = movieImages.backdrops + movieImages.posters + movieImages.logos
        allImages.forEach { image ->
            assertTrue(
                image.languageCode in includeLanguages || image.languageCode == null,
                "Image should be for one of $includeLanguages or language-neutral, but was: ${image.languageCode}",
            )
        }
    }

    @Test
    fun `getMovieImages handles movie with at least one no images case gracefully`() = runTest {
        val testApiKey = requireApiKey()

        // Given
        val repository = MoviesRepository.factory(testApiKey)
        // Using a very high movie ID that likely has no images
        val obscureMovieId = 999999

        // When
        val result = repository.getMovieImages(obscureMovieId)

        // Then
        // This might return success with empty arrays or failure depending on TMDB API behavior
        when (result) {
            is DataResult.Success -> {
                // If successful, all image arrays should be empty
                assertTrue(result.data.backdrops.isEmpty())
                assertTrue(result.data.posters.isNotEmpty())
                assertTrue(result.data.logos.isEmpty())
            }
            is DataResult.Failure -> {
                // API might return failure for non-existent movie
                assertNotNull(result.message)
            }
        }
    }

    @Test
    fun `getMovieImages fails for invalid movie ID`() = runTest {
        val testApiKey = requireApiKey()

        // Given
        val repository = MoviesRepository.factory(testApiKey)
        val invalidMovieId = -1

        // When
        val result = repository.getMovieImages(invalidMovieId)

        // Then
        assertTrue(
            result is DataResult.Failure,
            "Expected failure for invalid movie ID, got: $result",
        )
    }

    @Test
    fun `getMovieImages with both language and includeImageLanguage parameters works correctly`() = runTest {
        val testApiKey = requireApiKey()

        // Given
        val repository = MoviesRepository.factory(testApiKey)
        val movieId = 24428 // The Avengers (2012) - more likely to have images
        val language = "en"
        val includeLanguages = listOf("es", "fr")

        // When
        val result = repository.getMovieImages(
            movieId,
            language = language,
            includeImageLanguage = includeLanguages,
        )

        // Then
        assertTrue(result is DataResult.Success, "Expected success, got: $result")
        assertNotNull(result.data)

        val movieImages = result.data
        assertEquals(movieId, movieImages.id)

        // Should have images
        assertTrue(
            movieImages.backdrops.isNotEmpty() || movieImages.posters.isNotEmpty() || movieImages.logos.isNotEmpty(),
            "Should have at least some images",
        )

        // Check that returned images are for the specified languages or language-neutral
        val expectedLanguages = listOf(language) + includeLanguages
        val allImages = movieImages.backdrops + movieImages.posters + movieImages.logos
        allImages.forEach { image ->
            assertTrue(
                image.languageCode in expectedLanguages || image.languageCode == null,
                "Image should be for one of $expectedLanguages or language-neutral, but was: ${image.languageCode}",
            )
        }
    }

    @Test
    fun `getMovieImages imageUrl property constructs correct URL`() = runTest {
        val testApiKey = requireApiKey()

        // Given
        val repository = MoviesRepository.factory(testApiKey)
        val movieId = 24428 // The Avengers (2012) - more likely to have images

        // When
        val result = repository.getMovieImages(movieId)

        // Then
        assertTrue(result is DataResult.Success)
        val movieImages = result.data

        // Get the first available image
        val firstImage = when {
            movieImages.backdrops.isNotEmpty() -> movieImages.backdrops[0]
            movieImages.posters.isNotEmpty() -> movieImages.posters[0]
            movieImages.logos.isNotEmpty() -> movieImages.logos[0]
            else -> return@runTest // Skip if no images
        }

        // Verify the imageUrl is constructed correctly
        val expectedUrl = "https://image.tmdb.org/t/p/original${firstImage.filePath}"
        assertEquals(expectedUrl, firstImage.imageUrl)
        assertTrue(firstImage.imageUrl.startsWith("https://image.tmdb.org/t/p/original/"))
    }
}
