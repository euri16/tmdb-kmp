package dev.euryperez.tmdb.data.movies.mappers

import dev.euryperez.tmdb.core.models.common.Gender
import dev.euryperez.tmdb.core.models.movies.TmdbAlternativeTitle
import dev.euryperez.tmdb.core.models.movies.TmdbGenre
import dev.euryperez.tmdb.core.models.movies.TmdbMember
import dev.euryperez.tmdb.core.models.movies.TmdbMovie
import dev.euryperez.tmdb.core.models.movies.TmdbMovieAlternativeTitles
import dev.euryperez.tmdb.core.models.movies.TmdbMovieCredits
import dev.euryperez.tmdb.core.models.movies.TmdbMovieDetails
import dev.euryperez.tmdb.core.models.movies.TmdbMovieExternalIds
import dev.euryperez.tmdb.core.models.movies.TmdbMovieImage
import dev.euryperez.tmdb.core.models.movies.TmdbMovieImages
import dev.euryperez.tmdb.core.models.movies.TmdbProductionCompany
import dev.euryperez.tmdb.core.models.movies.TmdbProductionCountry
import dev.euryperez.tmdb.core.models.movies.TmdbSize
import dev.euryperez.tmdb.core.utils.extensions.localDateOrNull
import dev.euryperez.tmdb.data.movies.api.dtos.AlternativeTitleDTO
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

internal fun MovieCastMemberDTO.toDomain() = TmdbMember(
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
    department = null, // Cast members don't have department
    job = null, // Cast members don't have job
)

internal fun MovieCrewMemberDTO.toDomain() = TmdbMember(
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
    castId = null, // Crew members don't have cast_id
    character = null, // Crew members don't have character
    creditId = creditId,
    order = null, // Crew members don't have order
    department = department,
    job = job,
)

internal fun MovieAlternativeTitlesResponseDTO.toDomain() = TmdbMovieAlternativeTitles(
    id = id,
    titles = titles.map { it.toDomain() },
)

internal fun AlternativeTitleDTO.toDomain() = TmdbAlternativeTitle(
    countryCode = iso31661,
    title = title,
    type = type,
)

internal fun MovieExternalIdsResponseDTO.toDomain() = TmdbMovieExternalIds(
    id = id,
    imdbId = imdbId,
    wikidataId = wikidataId,
    facebookId = facebookId,
    instagramId = instagramId,
    twitterId = twitterId,
)

internal fun MovieImagesResponseDTO.toDomain() = TmdbMovieImages(
    id = id,
    backdrops = backdrops.map { it.toDomain() },
    posters = posters.map { it.toDomain() },
    logos = logos.map { it.toDomain() },
)

internal fun MovieImageDTO.toDomain() = TmdbMovieImage(
    aspectRatio = aspectRatio,
    filePath = filePath,
    size = TmdbSize(width = width, height = height),
    languageCode = iso6391,
    voteAverage = voteAverage,
    voteCount = voteCount,
)
