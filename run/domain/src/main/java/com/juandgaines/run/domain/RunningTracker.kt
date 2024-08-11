@file:OptIn(ExperimentalCoroutinesApi::class)

package com.juandgaines.run.domain

import com.juandgaines.core.domain.Timer
import com.juandgaines.core.domain.location.LocationTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RunningTracker(
    private val locationObserver: LocationObserver,
    private val applicationScope: CoroutineScope
) {

    private val _runData = MutableStateFlow(RunData())
    val runData = _runData
    private val isTracking = MutableStateFlow(false)

    private val isObservingLocation = MutableStateFlow(false)

    private val _elapsedTime = MutableStateFlow(Duration.ZERO)
    val elapsedTime = _elapsedTime.asStateFlow()

    val currentLocation = isObservingLocation.flatMapLatest { isObservingLocation->
        if (isObservingLocation){
            locationObserver.observeLocation(1000L)
        } else {
            flowOf()
        }
    }.stateIn(
        applicationScope,
        SharingStarted.Lazily,
        null
    )

    init {
        isTracking
            .onEach { isTracking ->
                if (!isTracking){
                    val newList = buildList {
                        addAll(_runData.value.locations)
                        add(emptyList<LocationTimestamp>())
                    }.toList()
                    _runData.update {
                        RunData(
                            distanceMeters = it.distanceMeters,
                            pace = it.pace,
                            locations = newList
                        )
                    }
                }
            }
            .flatMapLatest { isTracking->
                if (isTracking){
                    Timer.timeAndEmits()
                } else {
                    flowOf()
                }
            }
            .onEach {
                _elapsedTime.value += it
            }.launchIn(applicationScope)

        currentLocation
            .filterNotNull()
            .combineTransform(isTracking){ location, isTracking->
                if (isTracking){
                    emit(location)
                }
            }
            .zip(_elapsedTime){ location, elapsedTime->
                LocationTimestamp(
                    location = location,
                    durationTimestamp = elapsedTime
                )
            }
            .onEach { location ->
                val currentLocations = _runData.value.locations
                val lastLocationList = if (currentLocations.isNotEmpty()){
                    currentLocations.last() + location
                } else listOf(location)
                val newLocationList = currentLocations.replaceLast(lastLocationList)
                val distanceMeters = LocationDataCalculator.getTotalDistanceInMeters(
                    newLocationList
                )
                val distanceKm = distanceMeters / 1000.0
                val currentDuration = location.durationTimestamp
                val avgSecondsPerKm = if (distanceKm == 0.0){
                    0
                } else {
                    (currentDuration.inWholeSeconds / distanceKm).roundToInt()
                }
                _runData.update {
                    RunData(
                        distanceMeters = distanceMeters,
                        pace = avgSecondsPerKm.seconds,
                        locations = newLocationList
                    )
                }
            }.launchIn(applicationScope)
    }

    fun setIsTracking(isTracking: Boolean){
        this.isTracking.value = isTracking
    }

    fun startObservingLocation(){
        isObservingLocation.value = true
    }
    fun stopObservingLocation(){
        isObservingLocation.value = false
    }
}

private fun <T> List<List<T>>.replaceLast(replacement: List<T>): List<List<T>> {
    return if (this.isEmpty()){
        listOf(replacement)
    } else {
        dropLast(1) + listOf(replacement)
    }
}