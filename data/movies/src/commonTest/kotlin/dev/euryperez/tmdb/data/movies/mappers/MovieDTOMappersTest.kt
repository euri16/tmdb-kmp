package dev.euryperez.tmdb.data.movies.mappers

import dev.euryperez.tmdb.core.models.movies.TmdbAlternativeTitle
import dev.euryperez.tmdb.core.models.movies.TmdbMovieAlternativeTitles
import dev.euryperez.tmdb.core.models.movies.TmdbMovieExternalIds
import dev.euryperez.tmdb.core.test.BaseTest
import dev.euryperez.tmdb.core.test.rules.MainCoroutineRule
import dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieAlternativeTitlesResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieExternalIdsResponseDTO
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
}
