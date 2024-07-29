package com.juandgaines.core.ui

import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.DataError.LocalError.DISK_FULL
import com.juandgaines.core.domain.util.DataError.NetworkError.NO_INTERNET
import com.juandgaines.core.domain.util.DataError.NetworkError.PAYLOAD_TOO_LARGE
import com.juandgaines.core.domain.util.DataError.NetworkError.REQUEST_TIMEOUT
import com.juandgaines.core.domain.util.DataError.NetworkError.SERIALIZATION
import com.juandgaines.core.domain.util.DataError.NetworkError.SERVER_ERROR
import com.juandgaines.core.domain.util.DataError.NetworkError.TOO_MANY_REQUESTS

fun DataError.asUiText(): UiText {
    return when (this) {
        DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
        REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_request_timeout)
        TOO_MANY_REQUESTS -> UiText.StringResource(R.string.error_too_many_request)
        NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
        PAYLOAD_TOO_LARGE -> UiText.StringResource(R.string.error_payload_too_large)
        SERVER_ERROR -> UiText.StringResource(R.string.error_server_error)
        SERIALIZATION -> UiText.StringResource(R.string.error_serialization)
        else-> UiText.StringResource(R.string.error_unknown)
    }

}