package com.juandgaines.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
):ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _hasLocationPermission = MutableStateFlow(false)


    init{
        _hasLocationPermission
            .onEach { hasPermission->
                if(hasPermission){
                    runningTracker.startObservingLocation()
                }else{
                    runningTracker.stopObservingLocation()
                }
            }.launchIn(
                viewModelScope
            )

        runningTracker.currentLocation
            .onEach { location->
               Timber.d("New Location: $location")
            }.launchIn(
                viewModelScope
            )
    }

    fun onAction(action:ActiveRunAction){
        when(action){
            OnBackClick -> TODO()
            OnFinishRunClick -> TODO()
            OnResumeRunClick -> TODO()
            OnToggleRunClick -> TODO()
            is SubmitLocationPermissionInfo -> {
                _hasLocationPermission.value = action.acceptedLocationPermission
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
        }
    }
}