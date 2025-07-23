package dev.euryperez.tmdb.core.test.extensions

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel

fun MockEngine.Companion.test(
    defaultResponse: String = """{"results":[],"total_pages":1,"page":1,"total_results":0}""",
    status: HttpStatusCode = HttpStatusCode.OK,
    headers: Headers = headersOf(HttpHeaders.ContentType, "application/json"),
) = MockEngine { request ->
    respond(content = ByteReadChannel(defaultResponse), status = status, headers = headers)
}

fun MockEngine.Companion.test(
    path: String,
    expectedResponse: String,
    expectedStatusCode: HttpStatusCode = HttpStatusCode.OK,
    headers: Headers = headersOf(HttpHeaders.ContentType, "application/json"),
    onRequest: (request: HttpRequestData) -> Unit = { },
): HttpClientEngine {
    return MockEngine.create {
        addHandler { request ->
            when (request.url.encodedPath) {
                path -> {
                    onRequest(request)

                    respond(
                        content = ByteReadChannel(expectedResponse),
                        status = expectedStatusCode,
                        headers = headers,
                    )
                }

                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }
    }
}

fun MockEngine.Companion.test(throwable: Throwable): HttpClientEngine {
    return MockEngine.create {
        addHandler { request ->
            throw throwable
        }
    }
}
