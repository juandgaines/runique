package com.juandgaines.wear.run.data

import com.juandgaines.core.connectivity.domain.DeviceNode
import com.juandgaines.core.connectivity.domain.DeviceType.WATCH
import com.juandgaines.core.connectivity.domain.NodeDiscovery
import com.juandgaines.wear.run.domain.PhoneConnector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WatchToPhoneConnector(
    nodeDiscovery: NodeDiscovery,
    applicationScope: CoroutineScope
):PhoneConnector {
    private val _connectedNode = MutableStateFlow<DeviceNode?>(null)

    override val connectedNode: StateFlow<DeviceNode?> = _connectedNode.asStateFlow()

    val messagingActions = nodeDiscovery
        .observeConnectedDevices(WATCH)
        .onEach {connectedDevices->
            val node = connectedDevices.firstOrNull()
            if (node != null && node.isNearby){
                _connectedNode.value = node
            }
        }.launchIn(applicationScope)
}