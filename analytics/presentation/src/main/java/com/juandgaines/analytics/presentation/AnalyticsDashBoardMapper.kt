package com.juandgaines.analytics.presentation

import com.juandgaines.analytics.domain.AnalyticsValues
import com.juandgaines.core.presentation.ui.formatted
import com.juandgaines.core.presentation.ui.toFormattedKm
import com.juandgaines.core.presentation.ui.toFormattedKmh
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

fun Duration.toFormattedTotalTime(): String {
    val days = toLong(DurationUnit.DAYS)
    val hours = toLong(DurationUnit.HOURS) % 24
    val minutes = toLong(DurationUnit.MINUTES) % 60
    return "$days d $hours h $minutes m"
}

fun AnalyticsValues. toAnalyticsDashBoardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fasterEverRun.toFormattedKmh(),
        avgDistance = (avgDistancePerRun/1000.0).toFormattedKm(),
        avgPace = avgPacePerRun.seconds.formatted()
    )
}