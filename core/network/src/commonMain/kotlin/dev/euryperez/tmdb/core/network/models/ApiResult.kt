package dev.euryperez.tmdb.core.network.models

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>

    sealed interface Error : ApiResult<Nothing> {
        data class HttpError(val code: Int, val message: String) : Error
        data class NetworkError(val message: String?) : Error
        data class SerializationError(val message: String?) : Error
    }
}
