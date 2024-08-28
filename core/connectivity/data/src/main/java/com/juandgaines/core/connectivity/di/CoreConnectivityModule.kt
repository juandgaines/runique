package com.juandgaines.core.connectivity.di

import com.juandgaines.core.connectivity.domain.NodeDiscovery
import com.juandgaines.core.connectivity.data.WearNodeDiscovery
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreConnectivityDataModule = module {
    singleOf(::WearNodeDiscovery).bind<NodeDiscovery>()
}