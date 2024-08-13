package com.juandgaines.run.location.di

import com.juandgaines.run.domain.LocationObserver
import com.juandgaines.run.location.AndroidLocationObserver
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    singleOf(::AndroidLocationObserver).bind<LocationObserver>()

}