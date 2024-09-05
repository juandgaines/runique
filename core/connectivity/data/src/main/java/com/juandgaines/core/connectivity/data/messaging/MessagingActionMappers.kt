package com.juandgaines.core.connectivity.data.messaging

import com.juandgaines.core.connectivity.domain.messaging.MessagingAction

fun MessagingAction.toMessagingActionDto(): MessagingActionDto {
    return when (this) {
        is MessagingAction.StarOrResume -> MessagingActionDto.StarOrResume
        is MessagingAction.Pause -> MessagingActionDto.Pause
        is MessagingAction.Finish -> MessagingActionDto.Finish
        is MessagingAction.Trackable -> MessagingActionDto.Trackable
        is MessagingAction.Untrackable -> MessagingActionDto.Untrackable
        is MessagingAction.ConnectionRequest -> MessagingActionDto.ConnectionRequest
        is MessagingAction.HeartRateUpdate -> MessagingActionDto.HeartRateUpdate(heartRate)
        is MessagingAction.DistanceUpdate -> MessagingActionDto.DistanceUpdate(distanceMeters)
        is MessagingAction.TimeUpdate -> MessagingActionDto.TimeUpdate(elapsedTime)
    }
}

fun MessagingActionDto.toMessagingAction(): MessagingAction {
    return when (this) {
        is MessagingActionDto.StarOrResume -> MessagingAction.StarOrResume
        is MessagingActionDto.Pause -> MessagingAction.Pause
        is MessagingActionDto.Finish -> MessagingAction.Finish
        is MessagingActionDto.Trackable -> MessagingAction.Trackable
        is MessagingActionDto.Untrackable -> MessagingAction.Untrackable
        is MessagingActionDto.ConnectionRequest -> MessagingAction.ConnectionRequest
        is MessagingActionDto.HeartRateUpdate -> MessagingAction.HeartRateUpdate(heartRate)
        is MessagingActionDto.DistanceUpdate -> MessagingAction.DistanceUpdate(distanceMeters)
        is MessagingActionDto.TimeUpdate -> MessagingAction.TimeUpdate(elapsedTime)
    }
}