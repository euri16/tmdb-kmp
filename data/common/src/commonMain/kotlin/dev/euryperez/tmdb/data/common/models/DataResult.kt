package dev.euryperez.tmdb.data.common.models

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Failure(val message: String?) : DataResult<Nothing>

    fun <R : Any> map(mapper: (T) -> R) = when (this) {
        is Failure -> this
        is Success -> Success(mapper(data))
    }

    fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (String?) -> R
    ): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Failure -> onFailure(message)
        }
    }

    fun onSuccess(block: (T) -> Unit): DataResult<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (String?) -> Unit): DataResult<T> {
        if (this is Failure) block(message)
        return this
    }
}