package com.juandgaines.run.domain

import com.juandgaines.core.domain.location.LocationTimestamp
import kotlin.time.Duration

data class RunData(
    val distanceMeters:Int = 0,
    val pace: Duration = Duration.ZERO,
    val locations: List<List<LocationTimestamp>> = emptyList(),
    val heartRates: List<Int> = emptyList(),
)
