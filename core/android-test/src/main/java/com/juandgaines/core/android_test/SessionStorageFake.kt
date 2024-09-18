package com.juandgaines.core.android_test

import com.juandgaines.core.domain.AuthInfo
import com.juandgaines.core.domain.SessionStorage

class SessionStorageFake:SessionStorage {

    private var authInfo: AuthInfo? = null

    override suspend fun get(): AuthInfo? {
        return authInfo
    }

    override suspend fun set(info: AuthInfo?) {
        authInfo = info
    }
}