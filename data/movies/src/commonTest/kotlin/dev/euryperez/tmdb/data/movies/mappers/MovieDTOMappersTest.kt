package dev.euryperez.tmdb.data.movies.mappers

import dev.euryperez.tmdb.core.models.movies.TmdbAlternativeTitle
import dev.euryperez.tmdb.core.models.movies.TmdbMovieAlternativeTitles
import dev.euryperez.tmdb.core.models.movies.TmdbMovieExternalIds
import dev.euryperez.tmdb.core.models.movies.TmdbMovieImage
import dev.euryperez.tmdb.core.models.movies.TmdbMovieImages
import dev.euryperez.tmdb.core.models.movies.TmdbSize
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieAlternativeTitlesResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieExternalIdsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieImageDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieImagesResponseDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDTOMappersTest : BaseTest {

    val mainCoroutineRule = MainCoroutineRule()

    @BeforeTest
    override fun setup() {
        mainCoroutineRule.setup()
    }

    @AfterTest
    override fun tearDown() {
        mainCoroutineRule.tearDown()
    }

    // =========================
    // AlternativeTitleDTO Tests
    // =========================

    @Test
    fun `AlternativeTitleDTO toDomain maps all fields correctly`() {
        // Given
        val dto = AlternativeTitleDTO(
            iso31661 = "US",
            title = "Fight Club",
            type = "Original Title",
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = TmdbAlternativeTitle(
            countryCode = "US",
            title = "Fight Club",
            type = "Original Title",
        )
        assertEquals(expected, result)
    }

    @Test
    fun `AlternativeTitleDTO toDomain handles null type field`() {
        // Given
        val dto = AlternativeTitleDTO(
            iso31661 = "ES",
            title = "El club de la lucha",
            type = null,
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = TmdbAlternativeTitle(
            countryCode = "ES",
            title = "El club de la lucha",
            type = null,
        )
        assertEquals(expected, result)
    }

    @Test
    fun `AlternativeTitleDTO toDomain maps iso31661 to countryCode correctly`() {
        // Given
        val dto = AlternativeTitleDTO(
            iso31661 = "FR",
            title = "Fight Club",
            type = null,
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals("FR", result.countryCode)
        assertEquals("Fight Club", result.title)
        assertEquals(null, result.type)
    }

    @Test
    fun `AlternativeTitleDTO toDomain preserves all title information`() {
        // Given
        val dto = AlternativeTitleDTO(
            iso31661 = "DE",
            title = "Fight Club - Finché non sai",
            type = "Theatrical Title",
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals("DE", result.countryCode)
        assertEquals("Fight Club - Finché non sai", result.title)
        assertEquals("Theatrical Title", result.type)
    }

    // ==========================================
    // MovieAlternativeTitlesResponseDTO Tests
    // ==========================================

    @Test
    fun `MovieAlternativeTitlesResponseDTO toDomain maps all fields correctly`() {
        // Given
        val dto = MovieAlternativeTitlesResponseDTO(
            id = 550,
            titles = listOf(
                AlternativeTitleDTO(
                    iso31661 = "US",
                    title = "Fight Club",
                    type = "Original Title",
                ),
                AlternativeTitleDTO(
                    iso31661 = "ES",
                    title = "El club de la lucha",
                    type = null,
                ),
            ),
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = TmdbMovieAlternativeTitles(
            id = 550,
            titles = listOf(
                TmdbAlternativeTitle(
                    countryCode = "US",
                    title = "Fight Club",
                    type = "Original Title",
                ),
                TmdbAlternativeTitle(
                    countryCode = "ES",
                    title = "El club de la lucha",
                    type = null,
                ),
            ),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `MovieAlternativeTitlesResponseDTO toDomain handles empty titles list`() {
        // Given
        val dto = MovieAlternativeTitlesResponseDTO(
            id = 999,
            titles = emptyList(),
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = TmdbMovieAlternativeTitles(
            id = 999,
            titles = emptyList(),
        )
        assertEquals(expected, result)
        assertEquals(999, result.id)
        assertEquals(0, result.titles.size)
    }

    @Test
    fun `MovieAlternativeTitlesResponseDTO toDomain maps multiple alternative titles correctly`() {
        // Given
        val dto = MovieAlternativeTitlesResponseDTO(
            id = 550,
            titles = listOf(
                AlternativeTitleDTO(
                    iso31661 = "US",
                    title = "Fight Club",
                    type = "Original Title",
                ),
                AlternativeTitleDTO(
                    iso31661 = "ES",
                    title = "El club de la lucha",
                    type = null,
                ),
                AlternativeTitleDTO(
                    iso31661 = "FR",
                    title = "Fight Club",
                    type = null,
                ),
                AlternativeTitleDTO(
                    iso31661 = "DE",
                    title = "Fight Club",
                    type = "Theatrical Title",
                ),
            ),
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(550, result.id)
        assertEquals(4, result.titles.size)

        // Verify first title
        assertEquals("US", result.titles[0].countryCode)
        assertEquals("Fight Club", result.titles[0].title)
        assertEquals("Original Title", result.titles[0].type)

        // Verify second title
        assertEquals("ES", result.titles[1].countryCode)
        assertEquals("El club de la lucha", result.titles[1].title)
        assertEquals(null, result.titles[1].type)

        // Verify third title
        assertEquals("FR", result.titles[2].countryCode)
        assertEquals("Fight Club", result.titles[2].title)
        assertEquals(null, result.titles[2].type)

        // Verify fourth title
        assertEquals("DE", result.titles[3].countryCode)
        assertEquals("Fight Club", result.titles[3].title)
        assertEquals("Theatrical Title", result.titles[3].type)
    }

    @Test
    fun `MovieAlternativeTitlesResponseDTO toDomain preserves id field correctly`() {
        // Given
        val movieId = 12345
        val dto = MovieAlternativeTitlesResponseDTO(
            id = movieId,
            titles = listOf(
                AlternativeTitleDTO(
                    iso31661 = "IT",
                    title = "Fight Club - Test",
                    type = "Test Type",
                ),
            ),
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(movieId, result.id)
        assertEquals(1, result.titles.size)
        assertEquals("IT", result.titles[0].countryCode)
    }

    @Test
    fun `MovieAlternativeTitlesResponseDTO toDomain handles mixed type values`() {
        // Given
        val dto = MovieAlternativeTitlesResponseDTO(
            id = 550,
            titles = listOf(
                AlternativeTitleDTO(
                    iso31661 = "US",
                    title = "Fight Club",
                    type = "Original Title",
                ),
                AlternativeTitleDTO(
                    iso31661 = "ES",
                    title = "El club de la lucha",
                    type = null,
                ),
                AlternativeTitleDTO(
                    iso31661 = "FR",
                    title = "Fight Club",
                    type = "Alternative Title",
                ),
            ),
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(3, result.titles.size)
        assertEquals("Original Title", result.titles[0].type)
        assertEquals(null, result.titles[1].type)
        assertEquals("Alternative Title", result.titles[2].type)
    }

    // ========================================
    // MovieExternalIdsResponseDTO Tests
    // ========================================

    @Test
    fun `MovieExternalIdsResponseDTO toDomain maps all fields correctly`() {
        // Given
        val dto = MovieExternalIdsResponseDTO(
            id = 550,
            imdbId = "tt0137523",
            wikidataId = "Q190050",
            facebookId = "FightClubFilm",
            instagramId = "fightclubmovie",
            twitterId = "fightclub",
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = TmdbMovieExternalIds(
            id = 550,
            imdbId = "tt0137523",
            wikidataId = "Q190050",
            facebookId = "FightClubFilm",
            instagramId = "fightclubmovie",
            twitterId = "fightclub",
        )
        assertEquals(expected, result)
    }

    @Test
    fun `MovieExternalIdsResponseDTO toDomain handles all null external ids`() {
        // Given
        val dto = MovieExternalIdsResponseDTO(
            id = 999,
            imdbId = null,
            wikidataId = null,
            facebookId = null,
            instagramId = null,
            twitterId = null,
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(999, result.id)
        assertNull(result.imdbId)
        assertNull(result.wikidataId)
        assertNull(result.facebookId)
        assertNull(result.instagramId)
        assertNull(result.twitterId)
    }

    @Test
    fun `MovieExternalIdsResponseDTO toDomain handles partial external ids`() {
        // Given
        val dto = MovieExternalIdsResponseDTO(
            id = 123,
            imdbId = "tt1234567",
            wikidataId = null,
            facebookId = "partialmovie",
            instagramId = null,
            twitterId = "partialmovietwitter",
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(123, result.id)
        assertEquals("tt1234567", result.imdbId)
        assertNull(result.wikidataId)
        assertEquals("partialmovie", result.facebookId)
        assertNull(result.instagramId)
        assertEquals("partialmovietwitter", result.twitterId)
    }

    @Test
    fun `MovieExternalIdsResponseDTO toDomain preserves id field correctly`() {
        // Given
        val movieId = 12345
        val dto = MovieExternalIdsResponseDTO(
            id = movieId,
            imdbId = "tt9999999",
            wikidataId = "Q999999",
            facebookId = "testmovie",
            instagramId = "testmovieig",
            twitterId = "testmovietwitter",
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(movieId, result.id)
        assertEquals("tt9999999", result.imdbId)
        assertEquals("Q999999", result.wikidataId)
        assertEquals("testmovie", result.facebookId)
        assertEquals("testmovieig", result.instagramId)
        assertEquals("testmovietwitter", result.twitterId)
    }

    @Test
    fun `MovieExternalIdsResponseDTO toDomain handles only IMDb id`() {
        // Given
        val dto = MovieExternalIdsResponseDTO(
            id = 550,
            imdbId = "tt0137523",
            wikidataId = null,
            facebookId = null,
            instagramId = null,
            twitterId = null,
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(550, result.id)
        assertEquals("tt0137523", result.imdbId)
        assertNull(result.wikidataId)
        assertNull(result.facebookId)
        assertNull(result.instagramId)
        assertNull(result.twitterId)
    }

    @Test
    fun `MovieExternalIdsResponseDTO toDomain handles social media ids only`() {
        // Given
        val dto = MovieExternalIdsResponseDTO(
            id = 789,
            imdbId = null,
            wikidataId = null,
            facebookId = "socialmovie",
            instagramId = "socialmovieig",
            twitterId = "socialmovietwitter",
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(789, result.id)
        assertNull(result.imdbId)
        assertNull(result.wikidataId)
        assertEquals("socialmovie", result.facebookId)
        assertEquals("socialmovieig", result.instagramId)
        assertEquals("socialmovietwitter", result.twitterId)
    }

    // ============================
    // MovieImageDTO Tests
    // ============================

    @Test
    fun `MovieImageDTO toDomain maps all fields correctly`() {
        // Given
        val dto = MovieImageDTO(
            aspectRatio = 1.778,
            filePath = "/test-backdrop.jpg",
            height = 1080,
            width = 1920,
            iso6391 = "en",
            voteAverage = 8.5,
            voteCount = 250,
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = TmdbMovieImage(
            aspectRatio = 1.778,
            filePath = "/test-backdrop.jpg",
            size = TmdbSize(width = 1920, height = 1080),
            languageCode = "en",
            voteAverage = 8.5,
            voteCount = 250,
        )
        assertEquals(expected, result)
    }

    @Test
    fun `MovieImageDTO toDomain maps separate width height to TmdbSize correctly`() {
        // Given
        val dto = MovieImageDTO(
            aspectRatio = 0.667,
            filePath = "/test-poster.jpg",
            height = 750,
            width = 500,
            iso6391 = "es",
            voteAverage = 7.2,
            voteCount = 150,
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(TmdbSize(width = 500, height = 750), result.size)
        assertEquals(500, result.size.width)
        assertEquals(750, result.size.height)
    }

    @Test
    fun `MovieImageDTO toDomain maps iso6391 to languageCode correctly`() {
        // Given
        val dto = MovieImageDTO(
            aspectRatio = 2.0,
            filePath = "/test-logo.png",
            height = 200,
            width = 400,
            iso6391 = "fr",
            voteAverage = 6.8,
            voteCount = 75,
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals("fr", result.languageCode)
    }

    @Test
    fun `MovieImageDTO toDomain handles null iso6391 language code`() {
        // Given
        val dto = MovieImageDTO(
            aspectRatio = 1.5,
            filePath = "/neutral-logo.png",
            height = 300,
            width = 450,
            iso6391 = null,
            voteAverage = 9.0,
            voteCount = 500,
        )

        // When
        val result = dto.toDomain()

        // Then
        assertNull(result.languageCode)
        assertEquals(1.5, result.aspectRatio)
        assertEquals("/neutral-logo.png", result.filePath)
        assertEquals(TmdbSize(width = 450, height = 300), result.size)
        assertEquals(9.0, result.voteAverage)
        assertEquals(500, result.voteCount)
    }

    @Test
    fun `MovieImageDTO toDomain preserves all numeric values correctly`() {
        // Given
        val dto = MovieImageDTO(
            aspectRatio = 1.777777,
            filePath = "/precise-image.jpg",
            height = 1080,
            width = 1920,
            iso6391 = "de",
            voteAverage = 7.654321,
            voteCount = 999,
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(1.777777, result.aspectRatio)
        assertEquals(7.654321, result.voteAverage)
        assertEquals(999, result.voteCount)
        assertEquals(TmdbSize(width = 1920, height = 1080), result.size)
    }

    // =====================================
    // MovieImagesResponseDTO Tests
    // =====================================

    @Test
    fun `MovieImagesResponseDTO toDomain maps all image types correctly`() {
        // Given
        val backdropDto = MovieImageDTO(
            aspectRatio = 1.778,
            filePath = "/backdrop.jpg",
            height = 1080,
            width = 1920,
            iso6391 = "en",
            voteAverage = 8.0,
            voteCount = 200,
        )
        val posterDto = MovieImageDTO(
            aspectRatio = 0.667,
            filePath = "/poster.jpg",
            height = 750,
            width = 500,
            iso6391 = "es",
            voteAverage = 7.5,
            voteCount = 150,
        )
        val logoDto = MovieImageDTO(
            aspectRatio = 2.0,
            filePath = "/logo.png",
            height = 200,
            width = 400,
            iso6391 = null,
            voteAverage = 9.0,
            voteCount = 300,
        )

        val dto = MovieImagesResponseDTO(
            id = 550,
            backdrops = listOf(backdropDto),
            posters = listOf(posterDto),
            logos = listOf(logoDto),
        )

        // When
        val result = dto.toDomain()

        // Then
        val expected = TmdbMovieImages(
            id = 550,
            backdrops = listOf(backdropDto.toDomain()),
            posters = listOf(posterDto.toDomain()),
            logos = listOf(logoDto.toDomain()),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `MovieImagesResponseDTO toDomain handles empty image arrays`() {
        // Given
        val dto = MovieImagesResponseDTO(
            id = 999,
            backdrops = emptyList(),
            posters = emptyList(),
            logos = emptyList(),
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(999, result.id)
        assertEquals(0, result.backdrops.size)
        assertEquals(0, result.posters.size)
        assertEquals(0, result.logos.size)
    }

    @Test
    fun `MovieImagesResponseDTO toDomain handles multiple images of each type`() {
        // Given
        val backdrop1 = MovieImageDTO(
            aspectRatio = 1.778,
            filePath = "/backdrop1.jpg",
            height = 1080,
            width = 1920,
            iso6391 = "en",
            voteAverage = 8.5,
            voteCount = 250,
        )
        val backdrop2 = MovieImageDTO(
            aspectRatio = 1.778,
            filePath = "/backdrop2.jpg",
            height = 720,
            width = 1280,
            iso6391 = "es",
            voteAverage = 7.8,
            voteCount = 180,
        )
        val poster1 = MovieImageDTO(
            aspectRatio = 0.667,
            filePath = "/poster1.jpg",
            height = 750,
            width = 500,
            iso6391 = "en",
            voteAverage = 9.0,
            voteCount = 400,
        )
        val poster2 = MovieImageDTO(
            aspectRatio = 0.667,
            filePath = "/poster2.jpg",
            height = 1500,
            width = 1000,
            iso6391 = "fr",
            voteAverage = 8.2,
            voteCount = 320,
        )
        val logo1 = MovieImageDTO(
            aspectRatio = 2.5,
            filePath = "/logo1.png",
            height = 160,
            width = 400,
            iso6391 = null,
            voteAverage = 7.5,
            voteCount = 100,
        )

        val dto = MovieImagesResponseDTO(
            id = 550,
            backdrops = listOf(backdrop1, backdrop2),
            posters = listOf(poster1, poster2),
            logos = listOf(logo1),
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(550, result.id)
        assertEquals(2, result.backdrops.size)
        assertEquals(2, result.posters.size)
        assertEquals(1, result.logos.size)

        // Verify backdrops
        assertEquals("/backdrop1.jpg", result.backdrops[0].filePath)
        assertEquals("/backdrop2.jpg", result.backdrops[1].filePath)
        assertEquals("en", result.backdrops[0].languageCode)
        assertEquals("es", result.backdrops[1].languageCode)

        // Verify posters
        assertEquals("/poster1.jpg", result.posters[0].filePath)
        assertEquals("/poster2.jpg", result.posters[1].filePath)
        assertEquals(TmdbSize(width = 500, height = 750), result.posters[0].size)
        assertEquals(TmdbSize(width = 1000, height = 1500), result.posters[1].size)

        // Verify logos
        assertEquals("/logo1.png", result.logos[0].filePath)
        assertNull(result.logos[0].languageCode)
        assertEquals(TmdbSize(width = 400, height = 160), result.logos[0].size)
    }

    @Test
    fun `MovieImagesResponseDTO toDomain preserves movie id correctly`() {
        // Given
        val movieId = 12345
        val dto = MovieImagesResponseDTO(
            id = movieId,
            backdrops = listOf(
                MovieImageDTO(
                    aspectRatio = 1.778,
                    filePath = "/test.jpg",
                    height = 1080,
                    width = 1920,
                    iso6391 = "en",
                    voteAverage = 8.0,
                    voteCount = 100,
                ),
            ),
            posters = emptyList(),
            logos = emptyList(),
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(movieId, result.id)
    }

    @Test
    fun `MovieImagesResponseDTO toDomain handles mixed language images`() {
        // Given
        val englishImage = MovieImageDTO(
            aspectRatio = 1.778,
            filePath = "/english.jpg",
            height = 1080,
            width = 1920,
            iso6391 = "en",
            voteAverage = 8.0,
            voteCount = 200,
        )
        val spanishImage = MovieImageDTO(
            aspectRatio = 1.778,
            filePath = "/spanish.jpg",
            height = 1080,
            width = 1920,
            iso6391 = "es",
            voteAverage = 7.5,
            voteCount = 150,
        )
        val neutralImage = MovieImageDTO(
            aspectRatio = 2.0,
            filePath = "/neutral.png",
            height = 200,
            width = 400,
            iso6391 = null,
            voteAverage = 9.0,
            voteCount = 300,
        )

        val dto = MovieImagesResponseDTO(
            id = 550,
            backdrops = listOf(englishImage, spanishImage),
            posters = emptyList(),
            logos = listOf(neutralImage),
        )

        // When
        val result = dto.toDomain()

        // Then
        assertEquals(2, result.backdrops.size)
        assertEquals("en", result.backdrops[0].languageCode)
        assertEquals("es", result.backdrops[1].languageCode)
        assertEquals(1, result.logos.size)
        assertNull(result.logos[0].languageCode)
    }
}
