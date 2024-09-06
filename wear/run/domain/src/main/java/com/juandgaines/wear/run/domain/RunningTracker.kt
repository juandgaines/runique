@file:Suppress("OPT_IN_USAGE")

package com.juandgaines.wear.run.domain

import com.juandgaines.core.connectivity.domain.messaging.MessagingAction
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.ConnectionRequest
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.DistanceUpdate
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.Finish
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.HeartRateUpdate
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.Pause
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.StarOrResume
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.TimeUpdate
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.Trackable
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction.Untrackable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration

class RunningTracker(
    private val watchToPhoneConnector: PhoneConnector,
    private val exerciseTracker: ExerciseTracker,
    applicationScope: CoroutineScope
) {
    private val _heartRate = MutableStateFlow(0)
    val heartRate = _heartRate.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    private val _isTrackable = MutableStateFlow(false)
    val isTrackable = _isTrackable.asStateFlow()

    val distanceMeters = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.DistanceUpdate>()
        .map { it.distanceMeters }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            0
        )
    val elapsedTime = watchToPhoneConnector
        .messagingActions
        .filterIsInstance<MessagingAction.TimeUpdate>()
        .map { it.elapsedTime }
        .stateIn(
            applicationScope,
            SharingStarted.Lazily,
            Duration.ZERO
        )
    init {
        watchToPhoneConnector
            .messagingActions
            .onEach { action ->
                when(action){
                    Trackable -> {
                        _isTrackable.value = true
                    }
                    Untrackable -> {
                        _isTrackable.value = false
                    }
                    else -> Unit
                }
            }
            .launchIn(applicationScope)
        watchToPhoneConnector
            .connectedNode
            .filterNotNull()
            .onEach {
                exerciseTracker.prepareExercise()
            }
            .launchIn(applicationScope)
        isTracking
            .flatMapLatest { isTracking->
                if(isTracking){
                    exerciseTracker.heartRate
                }
                else flowOf()
            }
            .onEach { heartRate ->
                watchToPhoneConnector.sendActionToPhone(HeartRateUpdate(heartRate))
                _heartRate.value = heartRate
            }
            .launchIn(applicationScope)


    }
    fun setTracking(isTracking: Boolean) {
        _isTracking.value = isTracking
    }
}