package dev.euryperez.tmdb.core.models.movies

data class TmdbMovieAlternativeTitles(
    val id: Int,
    val titles: List<TmdbAlternativeTitle>,
)

data class TmdbAlternativeTitle(
    val countryCode: String,
    val title: String,
    val type: String? = null,
)
