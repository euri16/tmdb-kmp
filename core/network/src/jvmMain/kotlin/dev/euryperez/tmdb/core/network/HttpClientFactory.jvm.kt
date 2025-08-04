package dev.euryperez.tmdb.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
internal actual object HttpClientFactory {
    actual fun create(baseUrl: String, apiKey: String): HttpClient {
        return HttpClient(Apache5) { commonConfig(baseUrl, apiKey) }
    }
}
