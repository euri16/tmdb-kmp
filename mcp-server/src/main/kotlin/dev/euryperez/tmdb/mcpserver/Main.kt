package dev.euryperez.tmdb.mcpserver

import dev.euryperez.tmdb.data.movies.MoviesRepository
import dev.euryperez.tmdb.mcpserver.server.McpServer
import kotlinx.coroutines.runBlocking

fun main() {
    val apiKey = System.getenv("TMDB_API_KEY")!!
    val moviesRepository = MoviesRepository.create(apiKey = apiKey)
    val mcpServer = McpServer(moviesRepository)

    runBlocking {
        mcpServer.listen(onClose = { /*apiClient.close()*/ })

//        moviesRepository.getPopularMovies().onSuccess {
//            println(it)
//        }.onFailure {
//            println("Error fetching popular movies: $it")
//        }
        // moviesRepository.getUpcomingMovies().onSuccess { println(it) }
        // moviesRepository.getMovieDetails(603).onSuccess { println(it) }
        // moviesRepository.searchMovies("Ferrari").onSuccess { println(it) }
        // moviesRepository.getMovieCredits(603).onSuccess { println(it) }
    }
}
