package dev.euryperez.tmdb.mcpserver.server.tools

import dev.euryperez.tmdb.core.models.movies.TmdbMovieCredits
import dev.euryperez.tmdb.data.common.models.DataResult
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

fun Server.addMovieCreditsTool(
    getMovieCredits: suspend (movieId: Int, language: String) -> DataResult<TmdbMovieCredits>,
) {
        addTool(
            name = "get_movie_credits",
            description = "Retrieves the cast and crew members for a movie.",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("movieId") {
                        put("type", "number")
                        put("description", "The id of the movie to retrieve")
                    }
                    putJsonObject("language") {
                        put("type", "string")
                        put("description", "The language to retrieve the movies in. e.g. en-US")
                    }
                },
                required = listOf("page", "language")
            )
        ) { request ->
            val movieId = request.arguments["movieId"]?.jsonPrimitive?.intOrNull
            val language = request.arguments["language"]?.jsonPrimitive?.contentOrNull

            if (movieId == null) {
                return@addTool CallToolResult(
                    content = listOf(TextContent("The 'movieId' parameter is required."))
                )
            }

            if (language == null) {
                return@addTool CallToolResult(
                    content = listOf(TextContent("The 'language' parameter is required."))
                )
            }

            getMovieCredits(movieId, language).fold(
                onSuccess = { movieDetails ->
                    CallToolResult(
                        content = listOf(
                            TextContent(text = Json.Default.encodeToString(movieDetails))
                        )
                    )
                },
                onFailure = {
                    CallToolResult(
                        content = listOf(TextContent(text = it ?: "Unknown error"))
                    )
                }
            )
        }
    }