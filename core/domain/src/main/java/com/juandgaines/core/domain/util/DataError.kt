package com.juandgaines.core.domain.util

sealed interface DataError: Error {

    enum class NetworkError: DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION_ERROR,
        UNKNOWN
    }

    enum class LocalError: DataError {
        DISK_FULL,
        FILE_ERROR,
        UNKNOWN
    }
}