package dev.euryperez.tmdb.core.utils.extensions

import dev.euryperez.tmdb.integration.BuildConfig
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun getEnvironmentVariable(name: String): String? {
    return BuildConfig.TMDB_API_KEY
}
