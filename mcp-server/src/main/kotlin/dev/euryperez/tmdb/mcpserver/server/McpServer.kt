package dev.euryperez.tmdb.mcpserver.server

import dev.euryperez.tmdb.data.movies.MoviesRepository
import dev.euryperez.tmdb.mcpserver.server.tools.addMovieCreditsTool
import dev.euryperez.tmdb.mcpserver.server.tools.addMovieDetailsTool
import dev.euryperez.tmdb.mcpserver.server.tools.addPopularMoviesTool
import dev.euryperez.tmdb.mcpserver.server.tools.addSearchMoviesTool
import dev.euryperez.tmdb.mcpserver.server.tools.addUpcomingMoviesTool
import io.ktor.utils.io.streams.asInput
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.io.asSink
import kotlinx.io.buffered

class McpServer(private val moviesRepository: MoviesRepository) {
    suspend fun listen(onClose: () -> Unit) {
        val server = Server(
            Implementation(name = "tmdb", version = "1.0.0"),
            ServerOptions(
                capabilities = ServerCapabilities(
                    tools = ServerCapabilities.Tools(listChanged = true),
                ),
            ),
        )

        with(server) {
            addPopularMoviesTool(getPopularMovies = moviesRepository::getPopularMovies)

            addUpcomingMoviesTool(getUpcomingMovies = moviesRepository::getUpcomingMovies)

            addMovieDetailsTool(getMovieDetails = moviesRepository::getMovieDetails)

            addSearchMoviesTool(searchMovies = moviesRepository::searchMovies)

            addMovieCreditsTool(getMovieCredits = moviesRepository::getMovieCredits)
        }

        val transport = StdioServerTransport(
            System.`in`.asInput(),
            System.out.asSink().buffered(),
        )

        server.connect(transport)

        val done = Job()
        server.onClose {
            onClose()
            done.complete()
        }
        done.join()
    }
}
