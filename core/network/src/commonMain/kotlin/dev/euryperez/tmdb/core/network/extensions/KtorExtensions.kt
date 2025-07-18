package dev.euryperez.tmdb.core.network.extensions

import dev.euryperez.tmdb.core.network.models.ApiResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.serialization.JsonConvertException
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

suspend inline fun <reified R : Any, reified T> HttpClient.getAsApiResult(
    resource: R
): ApiResult<T> = try {
    val response = get(resource)
    when (response.status.value) {
        in 200..299 -> {
            try {
                ApiResult.Success(response.body<T>())
            } catch (e: SerializationException) {
                ApiResult.Error.SerializationError(e.message)
            } catch (e: JsonConvertException) {
                ApiResult.Error.SerializationError(e.message)
            }
        }
        in 400..499 -> {
            ApiResult.Error.HttpError(response.status.value, response.body<String>())
        }
        in 500..599 -> {
            ApiResult.Error.HttpError(response.status.value, response.body<String>())
        }
        else -> {
            ApiResult.Error.HttpError(response.status.value, response.body<String>())
        }
    }
} catch (e: IOException) {
    ApiResult.Error.NetworkError(e.message)
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    ApiResult.Error.NetworkError(e.message)
}
