package com.juandgaines.wear.run.data.di

import org.koin.dsl.module
import com.juandgaines.wear.run.data.HealthServicesExerciseTracker
import com.juandgaines.wear.run.data.WatchToPhoneConnector
import com.juandgaines.wear.run.domain.ExerciseTracker
import com.juandgaines.wear.run.domain.PhoneConnector
import com.juandgaines.wear.run.domain.RunningTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val wearDataModule = module {
    singleOf(::HealthServicesExerciseTracker).bind(ExerciseTracker::class)
    singleOf(::WatchToPhoneConnector).bind<PhoneConnector>()
    singleOf(::RunningTracker)
    single {
        get <RunningTracker>().elapsedTime
    }
}