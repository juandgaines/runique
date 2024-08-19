package com.juandgaines.analytics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AnalyticsDashBoardViewModel: ViewModel() {

    var state by mutableStateOf<AnalyticsDashboardState?>( null)
        private set

    fun onAction(action: AnalyticsAction) {
        when(action) {
            is AnalyticsAction.OnBackClick -> {


            }
        }
    }
}