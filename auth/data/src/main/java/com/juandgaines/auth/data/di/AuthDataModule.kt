package com.juandgaines.auth.data.di

import com.juandgaines.auth.data.EmailPatternValidator
import com.juandgaines.auth.domain.PatternValidator
import com.juandgaines.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val authModuleData = module {
    single<PatternValidator>{
        EmailPatternValidator
    }
    singleOf(::UserDataValidator)

}