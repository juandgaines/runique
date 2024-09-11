package com.juandgaines.core.database.mappers

import com.juandgaines.core.database.entity.RunEntity
import com.juandgaines.core.domain.location.Location
import com.juandgaines.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun():Run = Run(
    id = id,
    duration = durationMillis.milliseconds,
    dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
    distanceMeters = distanceMeters,
    location = Location(
        lat = latitude,
        long = longitude
    ),
    maxSpeedKmh = maxSpeedKmh,
    totalElevationMeters = totalElevationMeters,
    mapPictureUrl = mapPictureUrl,
    avgHeartRate = avgHeartRate,
    maxHeartRate = maxHeartRate
)

fun Run.toRunEntity():RunEntity = RunEntity(
    id = id ?: ObjectId().toHexString(),
    durationMillis = duration.inWholeMilliseconds,
    maxSpeedKmh = maxSpeedKmh,
    dateTimeUtc = dateTimeUtc.toInstant().toString(),
    latitude = location.lat,
    longitude = location.long,
    distanceMeters = distanceMeters,
    avgSpeedKmh = avgSpeedKmh,
    totalElevationMeters = totalElevationMeters,
    mapPictureUrl = mapPictureUrl,
    avgHeartRate = avgHeartRate,
    maxHeartRate = maxHeartRate
)