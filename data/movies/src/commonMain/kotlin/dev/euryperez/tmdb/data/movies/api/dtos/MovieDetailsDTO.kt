package dev.euryperez.tmdb.data.movies.api.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieDetailsDTO(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    val voteCount: Int,
    val popularity: Double,
    val genres: List<GenreDTO>,
    val adult: Boolean,
    @SerialName("original_language")
    val originalLanguage: String,
    @SerialName("original_title")
    val originalTitle: String,
    val video: Boolean,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompanyDTO> = emptyList(),
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountryDTO> = emptyList(),
    val revenue: Int?,
    val runtime: Int?,
    val budget: Int?,
    val homepage: String?,
)

@Serializable
internal data class ProductionCompanyDTO(
    val id: Int,
    val name: String,
)

@Serializable
internal data class ProductionCountryDTO(
    @SerialName("iso_3166_1")
    val isoCode: String,
    val name: String,
)

@Serializable
internal data class GenreDTO(
    val id: Int,
    val name: String,
)
