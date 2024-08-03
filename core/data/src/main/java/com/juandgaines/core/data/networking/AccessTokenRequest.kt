package com.juandgaines.core.data.networking

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenRequest(
    val userId: String,
    val refreshToken: String
)
