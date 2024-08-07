package com.juandgaines.run.presentation.di

import com.juandgaines.run.domain.RunningTracker
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.juandgaines.run.presentation.active_run.ActiveRunViewModel
import com.juandgaines.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val runPresentationModule = module {
    singleOf(::RunningTracker)
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}