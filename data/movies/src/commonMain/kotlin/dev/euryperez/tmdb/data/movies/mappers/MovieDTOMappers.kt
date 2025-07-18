package dev.euryperez.tmdb.data.movies.mappers

import dev.euryperez.tmdb.core.models.common.Gender
import dev.euryperez.tmdb.core.models.movies.TmdbGenre
import dev.euryperez.tmdb.core.models.movies.TmdbMember
import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.models.movies.TmdbMovieCredits
import dev.euryperez.tmdb.core.models.movies.TmdbMovieDetails
import dev.euryperez.tmdb.core.models.movies.TmdbProductionCompany
import dev.euryperez.tmdb.core.models.movies.TmdbProductionCountry
import dev.euryperez.tmdb.core.utils.extensions.localDateOrNull
import dev.euryperez.tmdb.data.movies.api.dtos.GenreDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieCreditsResponseDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieDetailsDTO
import dev.euryperez.tmdb.data.movies.api.dtos.MovieMemberDTO
import dev.euryperez.tmdb.data.movies.api.dtos.ProductionCompanyDTO
import dev.euryperez.tmdb.data.movies.api.dtos.ProductionCountryDTO

internal fun MovieDTO.toDomain(): TmdbMovie {
    return TmdbMovie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate.localDateOrNull(),
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        genreIds = genreIds,
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        video = video,
    )
}

internal fun MovieDetailsDTO.toDomain(): TmdbMovieDetails {
    return TmdbMovieDetails(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate.localDateOrNull(),
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        genres = genres.map { it.toDomain() },
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        video = video,
        productionCompanies = productionCompanies.map { it.toDomain() },
        productionCountries = productionCountries.map { it.toDomain() },
        revenue = revenue,
        runtime = runtime,
        budget = budget,
        homepage = homepage,
    )
}

internal fun GenreDTO.toDomain() = TmdbGenre(id = id, name = name)

internal fun ProductionCountryDTO.toDomain() = TmdbProductionCountry(isoCode = isoCode, name = name)

internal fun ProductionCompanyDTO.toDomain() = TmdbProductionCompany(id = id, name = name)

internal fun MovieCreditsResponseDTO.toDomain() = TmdbMovieCredits(
    id = id,
    cast = cast.map { it.toDomain() },
    crew = crew.map { it.toDomain() },
)

internal fun MovieMemberDTO.toDomain() = TmdbMember(
    id = id,
    name = name,
    adult = adult,
    gender = when (gender) {
        1 -> Gender.Female
        2 -> Gender.Male
        else -> Gender.Unknown
    },
    knownForDepartment = knownForDepartment,
    originalName = originalName,
    popularity = popularity,
    profilePath = profilePath,
    castId = castId,
    character = character,
    creditId = creditId,
    order = order,
    department = department,
    job = job,
)
