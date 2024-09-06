package com.juandgaines.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.juandgaines.core.domain.util.Result
import com.juandgaines.wear.run.domain.ExerciseTracker
import com.juandgaines.wear.run.domain.PhoneConnector
import com.juandgaines.wear.run.domain.RunningTracker
import com.juandgaines.wear.run.presentation.TrackerAction.OnBodySensorPermissionResult
import com.juandgaines.wear.run.presentation.TrackerAction.OnFinishRunClick
import com.juandgaines.wear.run.presentation.TrackerAction.OnToggleRunClick
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector,
    private val runningTracker: RunningTracker
):ViewModel() {

    var state by mutableStateOf(TrackerState())
        private set
    private val hasBodySensorPermission = MutableStateFlow(false)

    private val isTracking = snapshotFlow {
        state.isRunActive && state.isTrackable && state.isConnectedPhoneNearby
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )

    private val eventChannel = Channel<TrackerEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        phoneConnector
            .connectedNode
            .filterNotNull()
            .onEach { connectedNote->
                state = state.copy(isConnectedPhoneNearby = connectedNote.isNearby)
            }
            .combine(isTracking){_, isTracking->
                if(!isTracking){
                    phoneConnector.sendActionToPhone(MessagingAction.ConnectionRequest)
                }
            }
            .launchIn(viewModelScope)

        runningTracker.isTrackable
            .onEach { isTrackable ->
                state = state.copy(isTrackable = isTrackable)
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                val result = when{
                    isTracking && !state.hasStartedRunning ->{
                        exerciseTracker.startExercise()
                    }
                    isTracking && state.hasStartedRunning ->{
                        exerciseTracker.resumeExercise()
                    }
                    !isTracking && state.hasStartedRunning ->{
                        exerciseTracker.pauseExercise()
                    }
                    else -> Result.Success(Unit)
                }

                if (result is Result.Error){
                    result.error.toUiText()?.let {
                        eventChannel.send(TrackerEvent.Error(it))
                    }
                }

                if (isTracking) {
                    state = state.copy(hasStartedRunning = true)
                }

                runningTracker.setTracking(isTracking)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            val isHeartRateTrackingSupported = exerciseTracker.isHeartRateTrackingSupported()
            state = state.copy(canTrackHeartRate = isHeartRateTrackingSupported)
        }

        runningTracker
            .heartRate
            .onEach {
                state = state.copy(heartRate = it)
            }
            .launchIn(viewModelScope)

        runningTracker
            .distanceMeters
            .onEach {
                state = state.copy(distanceMeters = it)
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedDuration = it)
            }
            .launchIn(viewModelScope)

        listenToPhoneActions()
    }



    fun onAction(action: TrackerAction, triggeredOnPhone:Boolean = false){
        if (!triggeredOnPhone) {
            sendActionToPhone(action)
        }
        when(action){
            is OnBodySensorPermissionResult -> {
                hasBodySensorPermission.value = action.granted
                if (action.granted) {
                    viewModelScope.launch {
                       val isHeartRateTrackingSupported = exerciseTracker.isHeartRateTrackingSupported()
                          state = state.copy(canTrackHeartRate = isHeartRateTrackingSupported)
                    }
                }
            }
            OnFinishRunClick -> {
                viewModelScope.launch {
                    exerciseTracker.stopExercise()
                    eventChannel.send(TrackerEvent.RunFinished)
                    state = state.copy(
                        elapsedDuration = Duration.ZERO,
                        distanceMeters = 0,
                        heartRate = 0,
                        hasStartedRunning = false,
                        isRunActive = false
                    )
                }
            }
            OnToggleRunClick -> {
                if (state.isTrackable){
                    state = state.copy(
                        isRunActive = !state.isRunActive
                    )
                }
            }
        }
    }

    private fun sendActionToPhone(action: TrackerAction){
        viewModelScope.launch {
            val messagingAction = when(action){
                OnToggleRunClick -> {
                    if(state.isRunActive){
                        MessagingAction.Pause
                    }
                    else{
                        MessagingAction.StarOrResume
                    }
                }
                OnFinishRunClick -> MessagingAction.Finish
                else -> null
            }

            messagingAction?.let {
                val result = phoneConnector.sendActionToPhone(it)
                if(result is Result.Error){
                    println( "Tracker Error: ${result.error}")
                }
            }
        }

    }
    private fun listenToPhoneActions(){
        phoneConnector
            .messagingActions
            .onEach { action ->
                when(action){
                    Finish -> onAction(OnFinishRunClick, true)
                    Pause ->{
                        if (state.isTrackable){
                            state = state.copy(isRunActive = false)
                        }
                    }
                    StarOrResume -> {
                        if (state.isTrackable){
                            state = state.copy(isRunActive = true)
                        }
                    }
                    Trackable -> {
                        state = state.copy(isTrackable = true)
                    }
                    Untrackable -> {
                        state = state.copy(isTrackable = false)
                    }
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }
}