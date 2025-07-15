package dev.euryperez.tmdb.core.utils.extensions

import kotlinx.datetime.LocalDate

fun String.localDateOrNull(): LocalDate? {
    return runCatching { LocalDate.parse(this) }.getOrNull()
}
