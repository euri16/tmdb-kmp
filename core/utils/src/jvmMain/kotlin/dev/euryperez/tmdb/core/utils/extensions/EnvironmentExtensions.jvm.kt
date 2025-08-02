package dev.euryperez.tmdb.core.utils.extensions

/**
 * JVM implementation for getting environment variables.
 * Uses System.getenv() which is available on the JVM.
 */
actual fun getEnvironmentVariable(name: String): String? {
    return runCatching { System.getenv(name) }.getOrNull()
}
