package dev.euryperez.tmdb.core.utils.extensions

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

/**
 * iOS implementation for getting environment variables.
 * Uses POSIX getenv() function which is available on iOS.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun getEnvironmentVariable(name: String): String? {
    return runCatching { getenv(name)?.toKString() }.getOrNull()
}
