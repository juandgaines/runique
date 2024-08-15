package com.juandgaines.run.network.di

import com.juandgaines.core.domain.run.RemoteRunDataSource
import com.juandgaines.run.network.KtorRemoteRunDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::KtorRemoteRunDataSource).bind<RemoteRunDataSource>()
}