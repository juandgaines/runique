package com.juandgaines.analytics.presentation.di

import com.juandgaines.analytics.presentation.AnalyticsDashBoardViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val analyticsPresentationModule = module {
    viewModelOf(::AnalyticsDashBoardViewModel)
}