package dev.euryperez.tmdb.data.movies.api.resources

import io.ktor.resources.Resource

@Resource("/search")
class SearchResource {
    @Resource("movie")
    class MovieResource(
        val parent: SearchResource = SearchResource(),
        val query: String,
        val page: Int = 1,
        val language: String = "en-US",
        val year: Int? = null
    )
}