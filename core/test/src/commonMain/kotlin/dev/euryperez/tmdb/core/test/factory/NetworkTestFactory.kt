package dev.euryperez.tmdb.core.test.factory

import dev.euryperez.tmdb.core.test.extensions.test
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkTestFactory {
    fun httpClient(
        baseUrl: String = "https://api.themoviedb.org/3/",
        apiKey: String = "test_api_key",
        engine: HttpClientEngine = MockEngine.test()
    ): HttpClient {
        return HttpClient(engine) {
            install(Resources)

            defaultRequest {
                url(baseUrl)

                headers {
                    append("Accept", "application/json")
                    append("Authorization", "bearer $apiKey")
                }

                contentType(ContentType.Application.Json)
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
    }
}