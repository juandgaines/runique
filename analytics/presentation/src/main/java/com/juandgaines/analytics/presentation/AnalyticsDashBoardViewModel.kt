package com.juandgaines.analytics.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.analytics.domain.AnalyticsRepository
import kotlinx.coroutines.launch

class AnalyticsDashBoardViewModel(
    private val analyticsRepository: AnalyticsRepository
): ViewModel() {

    var state by mutableStateOf<AnalyticsDashboardState?>( null)
        private set

    init {
        viewModelScope.launch {
            state= analyticsRepository.getAnalyticsValues().toAnalyticsDashBoardState()
        }

    }

    fun onAction(action: AnalyticsAction) {
        when(action) {
            is AnalyticsAction.OnBackClick -> {


            }
        }
    }
}