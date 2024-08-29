package com.juandgaines.wear.app.presentation

import android.app.Application
import com.juandgaines.core.connectivity.di.coreConnectivityDataModule
import com.juandgaines.wear.app.presentation.di.appModule
import com.juandgaines.wear.run.data.di.wearDataModule
import com.juandgaines.wear.run.presentation.di.wearRunPresentationModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RuniqueApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                appModule,
                wearRunPresentationModule,
                wearDataModule,
                coreConnectivityDataModule
            )
        }
    }
}