package dev.euryperez.tmdb.core.models.movies

data class TmdbMovieExternalIds(
    val id: Int,
    val imdbId: String?,
    val wikidataId: String?,
    val facebookId: String?,
    val instagramId: String?,
    val twitterId: String?,
)
