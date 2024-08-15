package com.juandgaines.run.domain

import com.juandgaines.core.domain.location.LocationTimestamp
import kotlin.math.roundToInt
import kotlin.time.DurationUnit.HOURS

object LocationDataCalculator {

    fun getTotalDistanceInMeters(
        locations: List<List<LocationTimestamp>>
    ):Int{
        return locations
            .sumOf { timestampsPerLine->
                timestampsPerLine
                    .zipWithNext { location1, location2->
                        location1.location.location.distanceTo(location2.location.location)
                    }.sum().toInt()
            }
    }

    fun getMaxSpeedKmh(locations:List<List<LocationTimestamp>>): Double {
        return locations.maxOf { locationsSet->
            locationsSet.zipWithNext{ location1, location2 ->
                val distance = location1.location.location.distanceTo(
                    other = location2.location.location
                )
                val hoursDifference = (location2.durationTimestamp - location1.durationTimestamp)
                    .toDouble(HOURS)

                if(hoursDifference == 0.0){
                    0.0
                }
                else{
                    (distance/1000.0)/ hoursDifference
                }
            }.maxOrNull()?:0.0
        }
    }
    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>):Int{
        return locations.sumOf { locationSet->
            locationSet.zipWithNext{location1, location2->
                val altitude1= location1.location.altitude
                val altitude2= location2.location.altitude
                (altitude2-altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }
}