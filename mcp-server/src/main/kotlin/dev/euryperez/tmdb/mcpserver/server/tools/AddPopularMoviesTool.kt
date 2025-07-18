package dev.euryperez.tmdb.mcpserver.server.tools

import dev.euryperez.tmdb.core.models.movies.TmdbMovie
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

fun Server.addPopularMoviesTool(
    getPopularMovies: suspend (page: Int, language: String) -> DataResult<List<TmdbMovie>>,
) {
    addTool(
        name = "get_popular_movies",
        description = "Retrieves a list of the most popular movies on TMDB.".trimIndent(),
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                putJsonObject("page") {
                    put("type", "number")
                    put("description", "The page number to retrieve.")
                }
                putJsonObject("language") {
                    put("type", "string")
                    put("description", "The language to retrieve the movies in. e.g. en-US")
                }
            },
            required = listOf("page", "language"),
        ),
    ) { request ->
        val page = request.arguments["page"]?.jsonPrimitive?.intOrNull
        val language = request.arguments["language"]?.jsonPrimitive?.contentOrNull

        if (page == null) {
            return@addTool CallToolResult(
                content = listOf(TextContent("The 'page' parameter is required.")),
            )
        }

        if (language == null) {
            return@addTool CallToolResult(
                content = listOf(TextContent("The 'language' parameter is required.")),
            )
        }

        getPopularMovies(page, language).fold(
            onSuccess = { popularMovies ->
                CallToolResult(
                    content = popularMovies.map {
                        TextContent(text = Json.Default.encodeToString(it))
                    },
                )
            },
            onFailure = {
                CallToolResult(
                    content = listOf(TextContent(text = it ?: "Unknown error")),
                )
            },
        )
    }
}
