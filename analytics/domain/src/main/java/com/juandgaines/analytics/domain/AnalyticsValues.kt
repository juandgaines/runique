package com.juandgaines.analytics.domain

import kotlin.time.Duration

data class AnalyticsValues(
    val totalDistanceRun:Int = 0,
    val totalTimeRun:Duration = Duration.ZERO,
    val fasterEverRun:Double = 0.0,
    val avgDistancePerRun: Double = 0.0,
    val avgPacePerRun: Double = 0.0,
)
