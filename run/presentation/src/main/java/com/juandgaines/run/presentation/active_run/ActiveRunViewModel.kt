package com.juandgaines.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.run.domain.RunningTracker
import com.juandgaines.run.presentation.active_run.ActiveRunAction.OnBackClick
import com.juandgaines.run.presentation.active_run.ActiveRunAction.OnFinishRunClick
import com.juandgaines.run.presentation.active_run.ActiveRunAction.OnResumeRunClick
import com.juandgaines.run.presentation.active_run.ActiveRunAction.OnToggleRunClick
import com.juandgaines.run.presentation.active_run.ActiveRunAction.SubmitLocationPermissionInfo
import com.juandgaines.run.presentation.active_run.ActiveRunAction.SubmitNotificationPermissionInfo
import com.juandgaines.run.presentation.active_run.ActiveRunAction.onDismissRationaleDialog
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
):ViewModel() {

    var state by mutableStateOf(ActiveRunState(
        shouldTrack = ActiveRunService.isServiceActive && runningTracker.isTracking.value,
        hasStartedRunning = ActiveRunService.isServiceActive,
    ))
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            false
        )

    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ){ shouldTrack, hasLocationPermission->
        shouldTrack && hasLocationPermission
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    init{
        hasLocationPermission
            .onEach { hasPermission->
                if(hasPermission){
                    runningTracker.startObservingLocation()
                }else{
                    runningTracker.stopObservingLocation()
                }
            }.launchIn(
                viewModelScope
            )

        isTracking
            .onEach { isTracking->
                runningTracker.setIsTracking(isTracking)
            }.launchIn(
                viewModelScope
            )

        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(
                    currentLocation = it?.location
                )
            }.launchIn(
                viewModelScope
            )

        runningTracker
            .runData
            .onEach {
                state = state.copy(
                    runData = it
                )
            }.launchIn(
                viewModelScope
            )

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(
                    elapsedTime = it
                )
            }.launchIn(
                viewModelScope
            )
    }

    fun onAction(action:ActiveRunAction){
        when(action){
            OnBackClick -> {
                state = state.copy(
                    shouldTrack = false
                )
            }
            OnResumeRunClick -> {
                state = state.copy(
                    shouldTrack = true
                )
            }
            OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }
            is SubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocationPermission
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }
            is SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationRationale = action.showNotificationRationale
                )
            }
            onDismissRationaleDialog -> {
                state = state.copy(
                    showLocationRationale = false,
                    showNotificationRationale = false
                )
            }
            else -> {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive){
            runningTracker.stopObservingLocation()
        }
    }
}