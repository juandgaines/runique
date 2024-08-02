@file:OptIn(ExperimentalFoundationApi::class)
package com.juandgaines.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juandgaines.auth.domain.AuthRepository
import com.juandgaines.auth.domain.UserDataValidator
import com.juandgaines.auth.presentation.R
import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.Result
import com.juandgaines.core.presentation.ui.UiText
import com.juandgaines.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@Suppress("OPT_IN_USAGE_FUTURE_ERROR")

class RegisterViewModel(
    private val userDataValidator: UserDataValidator,
    private val authRepository: AuthRepository
):ViewModel() {
    var state by  mutableStateOf(RegisterState())
        private set

    private val eventChannel = Channel<RegisterEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

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
        when(action){
            is RegisterAction.OnRegisterClick -> register()
            is RegisterAction.OnTogglePassWordVisibilityClick -> state = state.copy(isPasswordVisible = !state.isPasswordVisible)
            is RegisterAction.OnLoginClick -> {

            }
        }
    }

    private fun register(){
        viewModelScope.launch {
            state = state.copy(isRegistering = true)
            val result = authRepository.register(
                email = state.email.text.toString().trim(),
                password = state.password.text.toString()
            )
            state = state.copy(isRegistering = false)
            when(result){
                is Result.Success -> {
                    eventChannel.send(RegisterEvent.RegistrationSuccess)
                }
                is Result.Error -> {
                    if(result.error == DataError.Network.CONFLICT){
                        eventChannel.send(
                            RegisterEvent.Error(
                                UiText.StringResource(
                                    R.string.error_email_exist
                                )
                            )
                        )
                    }
                    else{
                        eventChannel.send(RegisterEvent.Error(result.error.asUiText()))
                    }
                }
            }
        }
    }
}