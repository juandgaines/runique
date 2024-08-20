package com.juandgaines.analytics.data.di

import org.koin.core.module.dsl.singleOf
import com.juandgaines.analytics.data.RoomAnalyticsRepository
import com.juandgaines.analytics.domain.AnalyticsRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
}