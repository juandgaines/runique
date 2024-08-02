package com.juandgaines.auth.data

import com.juandgaines.auth.domain.AuthRepository
import com.juandgaines.core.data.networking.post
import com.juandgaines.core.domain.util.DataError.Network
import com.juandgaines.core.domain.util.EmptyResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient
):AuthRepository  {
    override suspend fun register(
        email: String,
        password: String,
    ): EmptyResult<Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/register",
            body = RegisterRequest(
                email = email,
                password= password
            )
        )
    }
}