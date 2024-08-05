package com.juandgaines.auth.presentation.di

import com.juandgaines.auth.presentation.register.RegisterViewModel
import com.juandgaines.auth.presentation.login.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val authViewModelModule = module {
    viewModelOf(::RegisterViewModel)
    viewModelOf(::LoginViewModel)
}