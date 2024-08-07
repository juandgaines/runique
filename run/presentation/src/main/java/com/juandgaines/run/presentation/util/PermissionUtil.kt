package com.juandgaines.run.presentation.util

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

fun ComponentActivity.shouldShowLocationPermissionRationale(): Boolean {
    return shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)
}

fun ComponentActivity.shouldShowPostNotificationPermissionRationale(): Boolean {
    return VERSION.SDK_INT >= VERSION_CODES.TIRAMISU &&
        shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)
}

private fun Context.hasPermission(permission:String):Boolean{
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

fun Context.hasLocationPermission(): Boolean {
    return hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
}

fun Context.hasNotificationPermission(): Boolean {
    return if(VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        hasPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    } else true
}