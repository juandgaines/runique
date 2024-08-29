package com.juandgaines.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.wear.run.domain.ExerciseTracker
import com.juandgaines.wear.run.domain.PhoneConnector
import com.juandgaines.wear.run.presentation.TrackerAction.OnBodySensorPermissionResult
import com.juandgaines.wear.run.presentation.TrackerAction.OnFinishRunClick
import com.juandgaines.wear.run.presentation.TrackerAction.OnToggleRunClick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker,
    private val phoneConnector: PhoneConnector
):ViewModel() {

    var state by mutableStateOf(TrackerState())
        private set

    init {
        phoneConnector
            .connectedNode
            .filterNotNull()
            .onEach { connectedNote->
                state = state.copy(isConnectedPhoneNearby = connectedNote.isNearby)
            }
            .launchIn(viewModelScope)
    }

    private val hasBodySensorPermission = MutableStateFlow(false)

    fun onAction(action: TrackerAction){
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
            OnToggleRunClick -> Unit
            OnFinishRunClick -> Unit
        }
    }
}