package com.juandgaines.wear.run.presentation

sealed interface TrackerEvent {
    data object RunFinished: TrackerEvent
}