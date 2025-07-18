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

@Suppress("LongMethod")
fun Server.addSearchMoviesTool(
    searchMovies: suspend (
        query: String,
        page: Int,
        language: String,
        year: Int?,
    ) -> DataResult<List<TmdbMovie>>,
) {
    addTool(
        name = "search_movies",
        description = "Search for movies by query",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                putJsonObject("query") {
                    put("type", "string")
                    put("description", "The query to search for")
                }
                putJsonObject("page") {
                    put("type", "number")
                    put("description", "The page number to retrieve.")
                }
                putJsonObject("language") {
                    put("type", "string")
                    put("description", "The language to retrieve the movies in. e.g. en-US")
                }
                putJsonObject("year") {
                    put("type", "number")
                    put("description", "The year to retrieve the movies from.")
                }
            },
            required = listOf("query", "page", "language"),
        ),
    ) { request ->
        val page = request.arguments["page"]?.jsonPrimitive?.intOrNull
        val year = request.arguments["year"]?.jsonPrimitive?.intOrNull
        val query = request.arguments["query"]?.jsonPrimitive?.contentOrNull
        val language = request.arguments["language"]?.jsonPrimitive?.contentOrNull

        if (query == null) {
            return@addTool CallToolResult(
                content = listOf(TextContent("The 'query' parameter is required.")),
            )
        }

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

        searchMovies(query, page, language, year).fold(
            onSuccess = { movies ->
                CallToolResult(
                    content = movies.map {
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
