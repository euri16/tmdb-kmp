package dev.euryperez.tmdb.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.credentialForTrust
import platform.Foundation.serverTrust

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object HttpClientFactory {

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun create(baseUrl: String, apiKey: String): HttpClient {
        return HttpClient(Darwin) {
            commonConfig(baseUrl, apiKey)

            engine {
                handleChallenge { session, task, challenge, completionHandler ->
                    val host = challenge.protectionSpace.host
                    val trust = challenge.protectionSpace.serverTrust

                    if (host == TMDB_DOMAIN && trust != null) {
                        val credential = NSURLCredential.credentialForTrust(trust)
                        completionHandler(NSURLSessionAuthChallengeUseCredential, credential)
                    } else {
                        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
                    }
                }
            }
        }
    }
}

private const val TMDB_DOMAIN = "api.themoviedb.org"
