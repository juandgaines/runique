package com.juandgaines.core.connectivity.domain.messaging

import com.juandgaines.core.domain.util.Error

enum class MessagingError:Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN
}