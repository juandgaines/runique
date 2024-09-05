package com.juandgaines.wear.run.domain

import com.juandgaines.core.connectivity.domain.DeviceNode
import com.juandgaines.core.connectivity.domain.messaging.MessagingAction
import com.juandgaines.core.connectivity.domain.messaging.MessagingError
import com.juandgaines.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PhoneConnector {
    val connectedNode: StateFlow<DeviceNode?>
    val messagingActions: Flow<MessagingAction>
    suspend fun sendActionToPhone(action: MessagingAction):EmptyResult<MessagingError>
}