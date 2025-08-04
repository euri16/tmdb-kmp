package dev.euryperez.tmdb.core.test

import dev.euryperez.tmdb.core.utils.extensions.getEnvironmentVariable
import kotlin.test.fail

interface BaseTest {
    fun setup()
    fun tearDown()
}

fun BaseTest.requireApiKey(): String {
    return getEnvironmentVariable("TMDB_API_KEY")
        ?: fail(
            "TMDB_API_KEY environment variable is required for integration tests. Set it with: export TMDB_API_KEY=\"your_api_key\"",
        )
}
