package com.juandgaines.auth.data

import com.juandgaines.auth.domain.AuthRepository
import com.juandgaines.core.data.networking.post
import com.juandgaines.core.domain.AuthInfo
import com.juandgaines.core.domain.SessionStorage
import com.juandgaines.core.domain.util.DataError.Network
import com.juandgaines.core.domain.util.EmptyResult
import com.juandgaines.core.domain.util.Result.Success
import com.juandgaines.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
):AuthRepository  {
    override suspend fun login(
        email: String,
        password: String,
    ): EmptyResult<Network> {
        val result = httpClient.post<LoginRequest,LoginResponse>(
            route = "/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        )
        if(result is Success){
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    userId = result.data.userId
                )
            )
        }
        return result.asEmptyDataResult()
    }

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