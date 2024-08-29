package com.juandgaines.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.core.domain.location.Location
import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.domain.run.RunRepository
import com.juandgaines.core.domain.util.Result.Error
import com.juandgaines.core.domain.util.Result.Success
import com.juandgaines.core.presentation.ui.asUiText
import com.juandgaines.run.domain.LocationDataCalculator
import com.juandgaines.run.domain.RunningTracker
import com.juandgaines.run.domain.WatchConnector
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val watchConnector: WatchConnector
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

        watchConnector.connectedDevice
            .filterNotNull()
            .onEach { connectedDevice->
                Timber.d("Connected device: ${connectedDevice.displayName}")
            }.launchIn(
                viewModelScope
            )

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
            OnFinishRunClick ->{
                state = state.copy(
                    isRunFinished = true,
                    isSavingRun = true
                )
            }
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
            is ActiveRunAction.OnRunProcessed->{
                finishRun(action.mapPictureBytes)
            }
            else -> {}
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val locations = state.runData.locations
        if( locations.isEmpty() || locations.first().size<=1){
            state = state.copy(isSavingRun = false)
            return
        }
        viewModelScope.launch {
            val run = Run(
                id = null,
                duration = state.elapsedTime,
                dateTimeUtc = ZonedDateTime.now()
                    .withZoneSameInstant(ZoneId.of("UTC")),
                distanceMeters = state.runData.distanceMeters,
                location = state.currentLocation?: Location(0.0,0.0),
                maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                mapPictureUrl = null
            )

            runningTracker.finishRun()

            when (val result = runRepository.upsertRun(run = run,mapPictureBytes)){
                is Error -> {
                    eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                }
                is Success -> {
                    eventChannel.send(ActiveRunEvent.RunSaved)
                }
            }

            state = state.copy(isSavingRun = false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive){
            runningTracker.stopObservingLocation()
        }
    }
}