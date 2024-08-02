package com.juandgaines.auth.domain

import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun register(email:String, password:String):EmptyResult<DataError.Network>
}