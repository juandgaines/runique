package com.juandgaines.run.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.juandgaines.run.presentation.active_run.ActiveRunViewModel
import com.juandgaines.run.presentation.run_overview.RunOverviewViewModel

val runViewModelModule = module {
    viewModelOf(::RunOverviewViewModel)
    viewModelOf(::ActiveRunViewModel)
}