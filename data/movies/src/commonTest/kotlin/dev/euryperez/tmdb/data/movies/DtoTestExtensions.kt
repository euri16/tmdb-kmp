package dev.euryperez.tmdb.data.movies

import dev.euryperez.tmdb.data.movies.api.dtos.DatesDTO
import dev.euryperez.tmdb.data.movies.api.dtos.GenreDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieListResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieMemberDTO
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

internal fun MovieMemberDTO.Companion.test(
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
    department: String? = null,
    job: String? = null,
    adult: Boolean = false,
): MovieMemberDTO = MovieMemberDTO(
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
    department = department,
    job = job,
)

internal fun MovieCreditsResponseDTO.Companion.test(
    id: Int = 42,
    cast: List<MovieMemberDTO> = listOf(MovieMemberDTO.test()),
    crew: List<MovieMemberDTO> = listOf(MovieMemberDTO.test()),
): MovieCreditsResponseDTO = MovieCreditsResponseDTO(
    id = id,
    cast = cast,
    crew = crew,
)
