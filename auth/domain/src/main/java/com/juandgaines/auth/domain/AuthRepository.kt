package com.juandgaines.auth.domain

import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun login(email:String, password:String):EmptyResult<DataError.Network>
    suspend fun register(email:String, password:String):EmptyResult<DataError.Network>
}