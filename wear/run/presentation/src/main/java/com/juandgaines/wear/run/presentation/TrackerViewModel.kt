package com.juandgaines.wear.run.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.wear.run.domain.ExerciseTracker
import com.juandgaines.wear.run.presentation.TrackerAction.OnBodySensorPermissionResult
import com.juandgaines.wear.run.presentation.TrackerAction.OnFinishRunClick
import com.juandgaines.wear.run.presentation.TrackerAction.OnToggleRunClick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TrackerViewModel(
    private val exerciseTracker: ExerciseTracker
):ViewModel() {

    var state by mutableStateOf(TrackerState(
        isConnectedPhoneNearby = true,
    ))
        private set

    private val hasBodySensorPermission = MutableStateFlow(false)

    init {
        viewModelScope.launch {
           hasBodySensorPermission.flatMapLatest { isGranted->
                if(isGranted){
                    exerciseTracker.heartRate
                }
               else
                   flowOf()
           }.onEach {
                state = state.copy(heartRate = it)
           }.launchIn(this)
        }
    }
    fun onAction(action: TrackerAction){
        when(action){
            is OnBodySensorPermissionResult -> {
                hasBodySensorPermission.value = action.granted
                if (action.granted) {
                    viewModelScope.launch {
                       val isHeartRateTrackingSupported = exerciseTracker.isHeartRateTrackingSupported()
                          state = state.copy(canTrackHeartRate = isHeartRateTrackingSupported)
                        exerciseTracker.prepareExercise()
                        exerciseTracker.startExercise()
                    }
                }
            }
            OnToggleRunClick -> Unit
            OnFinishRunClick -> Unit
        }
    }
}