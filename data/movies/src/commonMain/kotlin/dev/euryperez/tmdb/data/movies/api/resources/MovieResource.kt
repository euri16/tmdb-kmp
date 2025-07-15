package dev.euryperez.tmdb.data.movies.api.resources

import io.ktor.resources.Resource

@Resource("/movie")
internal class MovieResource {
    @Resource("{movieId}")
    class Id(
        val parent: MovieResource = MovieResource(),
        val movieId: Int,
        val language: String = "en-US"
    ) {
        @Resource("credits")
        class Credits(val parent: Id, val language: String = "en-US")
    }

    @Resource("popular")
    class Popular(
        val parent: MovieResource = MovieResource(),
        val page: Int = 1,
        val language: String = "en-US"
    )

    @Resource("upcoming")
    class Upcoming(
        val parent: MovieResource = MovieResource(),
        val page: Int = 1,
        val language: String = "en-US"
    )
}