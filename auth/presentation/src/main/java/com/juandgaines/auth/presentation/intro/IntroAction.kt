package com.juandgaines.auth.presentation.intro

import com.juandgaines.auth.presentation.login.LoginAction

sealed interface IntroAction {
    data object OnSignInClick : IntroAction
    data object OnSignUpClick : IntroAction
}