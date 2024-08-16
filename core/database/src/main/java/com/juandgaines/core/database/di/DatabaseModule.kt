package com.juandgaines.core.database.di

import androidx.room.Room
import com.juandgaines.core.database.RoomLocalRunDataSource
import com.juandgaines.core.database.RunDataBase
import com.juandgaines.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDataBase::class.java,
            "run.db"
        ).build()
    }
    single { get<RunDataBase>().runDao() }
    single { get<RunDataBase>().runPendingSyncDao() }

    singleOf(::RoomLocalRunDataSource).bind<LocalRunDataSource>()
}