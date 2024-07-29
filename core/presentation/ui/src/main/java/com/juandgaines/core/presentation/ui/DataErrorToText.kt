package com.juandgaines.core.presentation.ui

import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.DataError.LocalError.DISK_FULL
import com.juandgaines.core.domain.util.DataError.Network.NO_INTERNET
import com.juandgaines.core.domain.util.DataError.Network.PAYLOAD_TOO_LARGE
import com.juandgaines.core.domain.util.DataError.Network.REQUEST_TIMEOUT
import com.juandgaines.core.domain.util.DataError.Network.SERIALIZATION
import com.juandgaines.core.domain.util.DataError.Network.SERVER_ERROR
import com.juandgaines.core.domain.util.DataError.Network.TOO_MANY_REQUESTS
import com.juandgaines.core.presentation.ui.UiText.StringResource

fun DataError.asUiText(): UiText {
    return when (this) {
        DISK_FULL -> StringResource(R.string.error_disk_full)
        REQUEST_TIMEOUT -> StringResource(R.string.error_request_timeout)
        TOO_MANY_REQUESTS -> StringResource(R.string.error_too_many_request)
        NO_INTERNET -> StringResource(R.string.error_no_internet)
        PAYLOAD_TOO_LARGE -> StringResource(R.string.error_payload_too_large)
        SERVER_ERROR -> StringResource(R.string.error_server_error)
        SERIALIZATION -> StringResource(R.string.error_serialization)
        else-> StringResource(R.string.error_unknown)
    }

}