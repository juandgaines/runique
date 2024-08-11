@file:OptIn(ExperimentalMaterial3Api::class)

package com.juandgaines.run.presentation.active_run

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juandgaines.core.presentation.designsystem.RuniqueTheme
import com.juandgaines.core.presentation.designsystem.StartIcon
import com.juandgaines.core.presentation.designsystem.StopIcon
import com.juandgaines.core.presentation.designsystem.components.RuniqueActionButton
import com.juandgaines.core.presentation.designsystem.components.RuniqueDialog
import com.juandgaines.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.juandgaines.core.presentation.designsystem.components.RuniqueOutlinedActionButton
import com.juandgaines.core.presentation.designsystem.components.RuniqueScaffold
import com.juandgaines.core.presentation.designsystem.components.RuniqueToolbar
import com.juandgaines.run.presentation.R
import com.juandgaines.run.presentation.active_run.components.RunDataCard
import com.juandgaines.run.presentation.active_run.maps.TrackerMap
import com.juandgaines.run.presentation.util.hasLocationPermission
import com.juandgaines.run.presentation.util.hasNotificationPermission
import com.juandgaines.run.presentation.util.shouldShowLocationPermissionRationale
import com.juandgaines.run.presentation.util.shouldShowPostNotificationPermissionRationale
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel(),
) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}
@Composable
fun ActiveRunScreen(
    state: ActiveRunState,
    onAction : (ActiveRunAction) -> Unit = {},
) {

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val hasCourseLocation = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFineLocation = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms[Manifest.permission.POST_NOTIFICATIONS] == true
        } else {
            true
        }

        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowPostNotificationPermissionRationale()

        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCourseLocation && hasFineLocation,
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotification,
                showNotificationRationale = showNotificationRationale
            )
        )
    }

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowPostNotificationPermissionRationale()
        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission() ,
                showLocationRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )
        if (!showLocationRationale || !showNotificationRationale) {
            permissionLauncher.requestRuniquePermissions(context)
        }
    }
    RuniqueScaffold(
        withGradient = false,
        topAppBar = {
            RuniqueToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = { onAction(ActiveRunAction.OnBackClick) },
            )
        },
        floatingActionButton = {
            RuniqueFloatingActionButton(
                onClick = { onAction(ActiveRunAction.OnToggleRunClick) },
                icon =
                    if (state.shouldTrack){
                        StopIcon
                    }
                    else{
                        StartIcon
                    },
                iconSize = 20.dp,
                contentDescription = if (state.shouldTrack) {
                    stringResource(id = R.string.pause_run)
                } else {
                    stringResource(id = R.string.start_run)
                }
            )
        }
    ) { paddingValues ->

        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ){

            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = {},
                modifier = Modifier
                    .fillMaxSize()
            )
            RunDataCard(
                elapsedTime = state.elapsedTime,
                runData = state.runData,
                modifier = Modifier
                    .padding(16.dp)
                    .padding(paddingValues)
                    .fillMaxWidth()
            )

        }

        if(!state.shouldTrack && state.hasStartedRunning){
            RuniqueDialog(
                title = stringResource(id = R.string.running_is_paused),
                description = stringResource(id = R.string.resume_or_finish_run),
                onDismiss = { onAction(ActiveRunAction.OnResumeRunClick) },
                primaryButton = {
                   RuniqueActionButton(
                       text = stringResource(id = R.string.resume),
                       isLoading = false,
                       onClick = { onAction(ActiveRunAction.OnResumeRunClick) },
                       modifier = Modifier.weight(1f)
                   )
                },
                secondaryButton = {
                    RuniqueOutlinedActionButton(
                        text = stringResource(id = R.string.finish),
                        isLoading = false,
                        onClick = { onAction(ActiveRunAction.OnFinishRunClick) },
                        modifier =  Modifier.weight(1f)
                    )
                }
            )
        }
        if (state.showLocationRationale || state.showNotificationRationale) {
           RuniqueDialog(
               title = stringResource(id = R.string.permission_required),
               onDismiss = { /* normal dissmiss not allowed for permission*/ },
               description = when {
                   state.showLocationRationale && state.showNotificationRationale -> stringResource(id = R.string.location_and_notification_permission_rationale)
                   state.showLocationRationale -> stringResource(id = R.string.location_permission_rationale)
                   else-> stringResource(id = R.string.notification_permission_rationale)
               },
               primaryButton = {
                   RuniqueOutlinedActionButton(
                       text = stringResource(id = R.string.okay),
                       isLoading = false,
                       onClick ={
                           onAction(ActiveRunAction.onDismissRationaleDialog)
                           permissionLauncher.requestRuniquePermissions(context)
                       }
                   )
               }) {

           }
        }
    }
}

private fun ActivityResultLauncher<Array<String>>.requestRuniquePermissions(
    context: Context
){
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermissions =  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyArray()
    }

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermissions)
        }
        !hasLocationPermission -> {
            launch(locationPermissions)
        }
        !hasNotificationPermission -> {
            launch(notificationPermissions)
        }
    }
}

@Preview
@Composable
fun RunOverviewScreenRootPreview() {
    RuniqueTheme {
        ActiveRunScreen(
            state = ActiveRunState(),
            onAction = {}
        )
    }
}