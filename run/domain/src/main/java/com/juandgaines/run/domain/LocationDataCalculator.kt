package com.juandgaines.run.domain

import com.juandgaines.core.domain.location.LocationTimestamp

object LocationDataCalculator {

    fun getTotalDistaneInMeters(
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
}