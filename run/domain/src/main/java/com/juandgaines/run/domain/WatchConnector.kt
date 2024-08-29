package com.juandgaines.run.domain

import com.juandgaines.core.connectivity.domain.DeviceNode
import kotlinx.coroutines.flow.StateFlow

interface WatchConnector {
    val connectedDevice: StateFlow<DeviceNode?>
    fun setIsTrackable(isTrackable: Boolean)
}