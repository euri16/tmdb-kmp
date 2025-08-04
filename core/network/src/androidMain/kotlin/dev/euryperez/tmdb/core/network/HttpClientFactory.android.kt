package dev.euryperez.tmdb.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
internal actual object HttpClientFactory {
    actual fun create(baseUrl: String, apiKey: String): HttpClient {
        return HttpClient(OkHttp) { commonConfig(baseUrl, apiKey) }
    }
}
