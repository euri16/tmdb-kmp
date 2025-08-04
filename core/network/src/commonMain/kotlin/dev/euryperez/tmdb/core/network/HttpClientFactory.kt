package dev.euryperez.tmdb.core.network

import io.ktor.client.HttpClient

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object HttpClientFactory {
    @Suppress("unused")
    fun create(baseUrl: String, apiKey: String): HttpClient
}
