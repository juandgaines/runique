package com.juandgaines.run.presentation.run_overview.mappers

import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.presentation.ui.formatted
import com.juandgaines.core.presentation.ui.toFormattedKm
import com.juandgaines.core.presentation.ui.toFormattedKmh
import com.juandgaines.core.presentation.ui.toFormattedMeters
import com.juandgaines.core.presentation.ui.toFormattedPace
import com.juandgaines.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())

    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - HH:mma")
        .format(dateTimeInLocalTime)

    val distanceKm = distanceMeters / 1000.0

    return RunUi(
        id = id ?: "",
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        distance = distanceKm.toFormattedKm(),
        avgSpeed = avgSpeedKmh.toFormattedKmh(),
        maxSpeed = maxSpeedKmh.toFormattedKmh(),
        pace =duration.toFormattedPace(distanceKm),
        totalElevation =totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}