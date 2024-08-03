package com.juandgaines.core.data.auth

import com.juandgaines.core.domain.AuthInfo
import kotlinx.serialization.Serializable

@Serializable
data class AuthInfoSerializable(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)