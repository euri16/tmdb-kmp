package dev.euryperez.tmdb.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger

// TODO: get the base url from a properties file
// TODO: Make this function internal, provide it using Koin or another DI framework
fun buildHttpClient(
    baseUrl: String = "https://api.themoviedb.org/3/", // TODO: Pull from properties file
    apiKey: String
) = HttpClient {
    install(Resources)

    defaultRequest {
        url(baseUrl)


        headers {
            append("Accept", "application/json")
            append("Authorization", "bearer $apiKey")
        }

        contentType(ContentType.Application.Json)
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                // Comment when testing the mcp server
                KermitLogger.d("tmdb-kmp-http") { message }
            }
        }
        level = LogLevel.ALL
    }

    install(ContentNegotiation) {
        json(
            Json {
                encodeDefaults = true
                isLenient = true
                coerceInputValues = true
                ignoreUnknownKeys = true
            },
        )
    }
}
