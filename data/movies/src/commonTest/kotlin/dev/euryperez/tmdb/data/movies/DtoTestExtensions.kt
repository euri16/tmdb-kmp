package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO
import dev.euryperez.tmdb.data.movies.api.dtos.DatesDTO
import dev.euryperez.tmdb.data.movies.api.dtos.GenreDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieAlternativeTitlesResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCastMemberDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCrewMemberDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieExternalIdsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieImageDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieImagesResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.NowPlayingMoviesResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.UpcomingMoviesResponseDTO

internal fun MovieDTO.Companion.test(
    id: Int = 1,
    title: String = "Test Movie",
    overview: String = "Test overview",
    posterPath: String? = "/test-poster.jpg",
    backdropPath: String? = "/test-backdrop.jpg",
    releaseDate: String = "2023-05-02",
    voteAverage: Double = 8.5,
    voteCount: Int = 1000,
    popularity: Double = 100.0,
    genreIds: List<Int> = listOf(28, 12),
    adult: Boolean = false,
    originalLanguage: String = "en",
    originalTitle: String = title,
    video: Boolean = false,
) = MovieDTO(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity,
    genreIds = genreIds,
    adult = adult,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    video = video,
)

internal fun MovieListResponseDTO.Companion.test(
    page: Int = 1,
    movies: List<MovieDTO> = listOf(MovieDTO.test()),
    totalPages: Int = 1,
    totalResults: Int = movies.size,
) = MovieListResponseDTO(
    page = page,
    results = movies,
    totalPages = totalPages,
    totalResults = totalResults,
)

internal fun UpcomingMoviesResponseDTO.Companion.test(
    page: Int = 1,
    movies: List<MovieDTO> = listOf(MovieDTO.test()),
    totalPages: Int = 1,
    totalResults: Int = movies.size,
    dates: DatesDTO = DatesDTO.test(),
) = UpcomingMoviesResponseDTO(
    page = page,
    results = movies,
    totalPages = totalPages,
    totalResults = totalResults,
    dates = dates,
)

internal fun NowPlayingMoviesResponseDTO.Companion.test(
    page: Int = 1,
    movies: List<MovieDTO> = listOf(MovieDTO.test()),
    totalPages: Int = 1,
    totalResults: Int = movies.size,
    dates: DatesDTO = DatesDTO.test(),
) = NowPlayingMoviesResponseDTO(
    page = page,
    results = movies,
    totalPages = totalPages,
    totalResults = totalResults,
    dates = dates,
)

internal fun DatesDTO.Companion.test(maximum: String = "2023-12-31", minimum: String = "2023-01-01") = DatesDTO(
    maximum = maximum,
    minimum = minimum,
)

internal fun MovieDetailsDTO.Companion.test(): MovieDetailsDTO = MovieDetailsDTO(
    id = 42,
    title = "The Answer",
    overview = "Life, the universe and everything.",
    posterPath = "/answer.jpg",
    backdropPath = "/backdrop.jpg",
    releaseDate = "1979-03-04",
    voteAverage = 9.9,
    voteCount = 420_000,
    popularity = 1000.0,
    genres = listOf(GenreDTO.test()),
    adult = false,
    originalLanguage = "en",
    originalTitle = "The Answer",
    video = false,
    productionCompanies = emptyList(),
    productionCountries = emptyList(),
    revenue = 1337,
    runtime = 113,
    budget = 4242,
    homepage = "https://example.com/the-answer",
)

internal fun GenreDTO.Companion.test(id: Int = 14, name: String = "Sci‑Fi") = GenreDTO(id, name)

internal fun MovieCastMemberDTO.Companion.test(
    id: Int = 1,
    name: String = "John Doe",
    gender: Int? = 2, // 0 = unspecified, 1 = female, 2 = male, 3 = non-binary
    knownForDepartment: String = "Acting",
    popularity: Double = 10.0,
    profilePath: String? = "/john_doe.jpg",
    castId: Int? = 7,
    character: String? = "Hero",
    creditId: String = "credit-$id",
    order: Int? = 0,
    adult: Boolean = false,
): MovieCastMemberDTO = MovieCastMemberDTO(
    adult = adult,
    gender = gender,
    id = id,
    knownForDepartment = knownForDepartment,
    name = name,
    originalName = name,
    popularity = popularity,
    profilePath = profilePath,
    castId = castId,
    character = character,
    creditId = creditId,
    order = order,
)

internal fun MovieCrewMemberDTO.Companion.test(
    id: Int = 2,
    name: String = "Jane Smith",
    gender: Int? = 1, // 0 = unspecified, 1 = female, 2 = male, 3 = non-binary
    knownForDepartment: String = "Directing",
    popularity: Double = 8.0,
    profilePath: String? = "/jane_smith.jpg",
    creditId: String = "credit-$id",
    department: String = "Directing",
    job: String = "Director",
    adult: Boolean = false,
): MovieCrewMemberDTO = MovieCrewMemberDTO(
    adult = adult,
    gender = gender,
    id = id,
    knownForDepartment = knownForDepartment,
    name = name,
    originalName = name,
    popularity = popularity,
    profilePath = profilePath,
    creditId = creditId,
    department = department,
    job = job,
)

internal fun MovieCreditsResponseDTO.Companion.test(
    id: Int = 42,
    cast: List<MovieCastMemberDTO> = listOf(MovieCastMemberDTO.test()),
    crew: List<MovieCrewMemberDTO> = listOf(MovieCrewMemberDTO.test()),
): MovieCreditsResponseDTO = MovieCreditsResponseDTO(
    id = id,
    cast = cast,
    crew = crew,
)

internal fun AlternativeTitleDTO.Companion.test(
    iso31661: String = "US",
    title: String = "Alternative Title",
    type: String? = null,
): AlternativeTitleDTO = AlternativeTitleDTO(
    iso31661 = iso31661,
    title = title,
    type = type,
)

internal fun MovieAlternativeTitlesResponseDTO.Companion.test(
    id: Int = 550,
    titles: List<AlternativeTitleDTO> = listOf(AlternativeTitleDTO.test()),
): MovieAlternativeTitlesResponseDTO = MovieAlternativeTitlesResponseDTO(
    id = id,
    titles = titles,
)

internal fun MovieExternalIdsResponseDTO.Companion.test(
    id: Int = 550,
    imdbId: String? = "tt0137523",
    wikidataId: String? = "Q190050",
    facebookId: String? = "FightClubFilm",
    instagramId: String? = "fightclubmovie",
    twitterId: String? = "fightclub",
): MovieExternalIdsResponseDTO = MovieExternalIdsResponseDTO(
    id = id,
    imdbId = imdbId,
    wikidataId = wikidataId,
    facebookId = facebookId,
    instagramId = instagramId,
    twitterId = twitterId,
)

internal fun MovieImageDTO.Companion.test(
    aspectRatio: Double = 1.778,
    filePath: String = "/test-image.jpg",
    height: Int = 1080,
    width: Int = 1920,
    iso6391: String? = "en",
    voteAverage: Double = 7.5,
    voteCount: Int = 150,
): MovieImageDTO = MovieImageDTO(
    aspectRatio = aspectRatio,
    filePath = filePath,
    height = height,
    width = width,
    iso6391 = iso6391,
    voteAverage = voteAverage,
    voteCount = voteCount,
)

internal fun MovieImagesResponseDTO.Companion.test(
    id: Int = 550,
    backdrops: List<MovieImageDTO> = listOf(
        MovieImageDTO.test(filePath = "/backdrop1.jpg", aspectRatio = 1.778),
        MovieImageDTO.test(filePath = "/backdrop2.jpg", aspectRatio = 1.778),
    ),
    posters: List<MovieImageDTO> = listOf(
        MovieImageDTO.test(filePath = "/poster1.jpg", aspectRatio = 0.667, width = 500, height = 750),
        MovieImageDTO.test(filePath = "/poster2.jpg", aspectRatio = 0.667, width = 500, height = 750),
    ),
    logos: List<MovieImageDTO> = listOf(
        MovieImageDTO.test(filePath = "/logo1.png", aspectRatio = 2.0, width = 400, height = 200, iso6391 = null),
    ),
): MovieImagesResponseDTO = MovieImagesResponseDTO(
    id = id,
    backdrops = backdrops,
    posters = posters,
    logos = logos,
)
