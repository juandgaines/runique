package com.juandgaines.core.data.di

import com.juandgaines.core.data.auth.EncryptedSessionStorage
import com.juandgaines.core.data.networking.HttpClientFactory
import com.juandgaines.core.domain.SessionStorage
import com.juandgaines.core.domain.run.RunRepository
import com.juandgaines.core.data.run.OfflineFirstRunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}