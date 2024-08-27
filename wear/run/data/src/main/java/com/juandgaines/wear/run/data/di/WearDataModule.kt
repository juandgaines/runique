package com.juandgaines.wear.run.data.di

import org.koin.dsl.module
import com.juandgaines.wear.run.data.HealthServicesExerciseTracker
import com.juandgaines.wear.run.domain.ExerciseTracker
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val wearDataModule = module {
    singleOf(::HealthServicesExerciseTracker).bind(ExerciseTracker::class)
}