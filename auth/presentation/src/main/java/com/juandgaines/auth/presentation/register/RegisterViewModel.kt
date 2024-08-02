@file:OptIn(ExperimentalFoundationApi::class)
package com.juandgaines.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi

import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.auth.domain.UserDataValidator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("OPT_IN_USAGE_FUTURE_ERROR")

class RegisterViewModel(
    private val userDataValidator: UserDataValidator
):ViewModel() {
    var state by  mutableStateOf(RegisterState())
        private set

    init {
        state.email.textAsFlow()
            .onEach { email ->
                val isEmailValid = userDataValidator.isValidEmail(email.toString())

                state = state.copy(
                    isEmailValid = userDataValidator.isValidEmail(email.toString()),
                    canRegister = isEmailValid && state.passwordValidationState.isValidPassword && !state.isRegistering
                )
            }
            .launchIn(viewModelScope)

        state.password.textAsFlow()
            .onEach { password ->
                val isValidPassword = userDataValidator.validatePassword(password.toString())
                state = state.copy(
                    passwordValidationState = isValidPassword,
                    canRegister = state.isEmailValid && !state.isRegistering && isValidPassword.isValidPassword
                )
            }.launchIn(viewModelScope)
    }
    fun onAction(action:RegisterAction){

    }
}