package com.juandgaines.auth.presentation.login

import com.juandgaines.core.presentation.ui.UiText

sealed interface LoginEvents {
    data class Error(val message: UiText) : LoginEvents
    data object LoginSuccess : LoginEvents
}