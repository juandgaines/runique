package com.juandgaines.analytics.presentation

sealed interface AnalyticsAction {
    data object OnBackClick : AnalyticsAction
}