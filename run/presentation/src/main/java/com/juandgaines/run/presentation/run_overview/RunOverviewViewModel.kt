package com.juandgaines.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.core.domain.run.RunRepository
import com.juandgaines.run.presentation.run_overview.RunOverviewAction.DeleteRun
import com.juandgaines.run.presentation.run_overview.RunOverviewAction.OnAnalyticsClick
import com.juandgaines.run.presentation.run_overview.RunOverviewAction.OnLogoutClick
import com.juandgaines.run.presentation.run_overview.RunOverviewAction.OnStartClick
import com.juandgaines.run.presentation.run_overview.mappers.toRunUi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository
):ViewModel() {

    var state by mutableStateOf(RunOverViewState())

    init{
        runRepository.getRuns().onEach { runs->
            val runsUi = runs.map {it.toRunUi()}
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.fetchRuns()
        }
    }
    fun onAction(action:RunOverviewAction) {
        when(action){
            is DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            OnAnalyticsClick -> Unit
            OnLogoutClick -> Unit
            OnStartClick -> Unit
        }
    }
}