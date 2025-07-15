package dev.euryperez.tmdb.data.common.extensions

import dev.euryperez.tmdb.data.common.models.DataResult
import dev.euryperez.tmdb.core.network.models.ApiResult
import dev.euryperez.tmdb.core.network.models.ApiResult.Error.HttpError
import dev.euryperez.tmdb.core.network.models.ApiResult.Error.NetworkError
import dev.euryperez.tmdb.core.network.models.ApiResult.Error.SerializationError

fun <T> ApiResult<T>.toDataResult(): DataResult<T> {
    return when (this) {
        is ApiResult.Success -> DataResult.Success(this.data)

        is ApiResult.Error -> when (this) {
            is HttpError -> DataResult.Failure(message = this.message)
            is NetworkError -> DataResult.Failure(message = this.message)
            is SerializationError -> DataResult.Failure(message = this.message)
        }
    }
}