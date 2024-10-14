@file:OptIn(ExperimentalFoundationApi::class)

package com.juandgaines.auth.presentation.login

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
import com.juandgaines.auth.presentation.login.LoginAction.OnLoginClick
import com.juandgaines.auth.presentation.login.LoginAction.OnTogglePasswordVisibility
import com.juandgaines.auth.presentation.login.LoginEvents.LoginSuccess
import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.Result
import com.juandgaines.core.domain.util.Result.Success
import com.juandgaines.core.presentation.ui.UiText
import com.juandgaines.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
) :ViewModel(){

    var state by mutableStateOf(LoginState())
        private set

    private val eventChannel = Channel<LoginEvents>()
    val events = eventChannel.receiveAsFlow()

    init {
        combine(state.email.textAsFlow(), state.password.textAsFlow()){ email, password ->

            state = state.copy(
                canLogin = userDataValidator.isValidEmail(email.toString()) &&
                    password.isNotEmpty(),
            )
        }.launchIn(viewModelScope)

    }

    fun onAction(action: LoginAction){
        when (action){
            OnLoginClick -> login()
            OnTogglePasswordVisibility -> {
                state = state.copy(isPasswordVisible = !state.isPasswordVisible)

            }
            else -> Unit
        }
    }

    private fun login(){
        viewModelScope.launch {

            state = state.copy(isLoggingIn = true)
            val result = authRepository.login(
                state.email.text.toString().trim(),
                state.password.text.toString()
            )

            state = state.copy(isLoggingIn = false)
            when (result){
                is Result.Error -> {
                    if(result.error == DataError.Network.UNAUTHORIZED){
                        eventChannel.send(
                            LoginEvents.Error(
                                UiText.StringResource(R.string.error_email_password_incorrect)
                            )
                        )
                    }
                    else{
                        eventChannel.send(
                            LoginEvents.Error(
                                result.error.asUiText()
                            )
                        )
                    }
                }
                is Success -> {
                    eventChannel.send(LoginSuccess)
                }
            }
        }
    }

}