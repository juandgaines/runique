package com.juandgaines.wear.run.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.juandgaines.wear.run.presentation.TrackerViewModel

val wearRunPresentationModule = module{
    viewModelOf(::TrackerViewModel)
}