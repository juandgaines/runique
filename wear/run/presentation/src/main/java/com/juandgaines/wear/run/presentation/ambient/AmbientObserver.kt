package com.juandgaines.wear.run.presentation.ambient

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.wear.ambient.AmbientLifecycleObserver
import androidx.wear.ambient.AmbientLifecycleObserver.AmbientDetails
import androidx.wear.ambient.AmbientLifecycleObserver.AmbientLifecycleCallback

@Composable
fun AmbientObserver(
    onEnterAmbient:(AmbientDetails) -> Unit,
    onExitAmbient:() -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(key1 = lifecycle) {
        val callback = object:AmbientLifecycleCallback{
            override fun onEnterAmbient(ambientDetails: AmbientDetails) {
                super.onEnterAmbient(ambientDetails)
                onEnterAmbient(ambientDetails)
            }

            override fun onExitAmbient() {
                super.onExitAmbient()
                onExitAmbient()
            }
        }
        val observer = AmbientLifecycleObserver(context as ComponentActivity, callback)
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}