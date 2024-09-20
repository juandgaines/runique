package com.juandgaines.test

import com.juandgaines.core.connectivity.domain.DeviceNode
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction
import com.juandgaines.core.connectivity.domain.messaging.MessagingError
import com.juandgaines.core.domain.util.EmptyResult
import com.juandgaines.core.domain.util.Result
import com.juandgaines.run.domain.WatchConnector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class PhoneToWatchConnectorFake :WatchConnector {

    var sendError:MessagingError? = null

    private val _isTrackable = MutableStateFlow(false)
    private val _connectedDevice = MutableStateFlow<DeviceNode?>(null)

    override val connectedDevice: StateFlow<DeviceNode?>
        get() = _connectedDevice.asStateFlow()

    private val _messagingActions = MutableSharedFlow<MessagingAction>()

    override val messagingActions: Flow<MessagingAction>
        get() = _messagingActions.asSharedFlow()

    override suspend fun sendActionToWatch(action: MessagingAction): EmptyResult<MessagingError> {
        return if (sendError == null){
            Result.Success(Unit)
        }
        else{
            Result.Error(sendError!!)
        }
    }

    override fun setIsTrackable(isTrackable: Boolean) {
        this._isTrackable.value = isTrackable
    }

    suspend fun sendFromWatchToPhone(action: MessagingAction){
        _messagingActions.emit(action)
    }
}