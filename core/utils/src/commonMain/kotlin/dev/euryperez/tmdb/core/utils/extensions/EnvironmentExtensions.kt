package dev.euryperez.tmdb.core.utils.extensions

/**
 * Gets an environment variable value by name.
 * This is a multiplatform function with platform-specific implementations.
 *
 * @param name The name of the environment variable
 * @return The environment variable value, or null if not found or not supported on the platform
 */
expect fun getEnvironmentVariable(name: String): String?
