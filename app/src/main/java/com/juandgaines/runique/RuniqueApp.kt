package com.juandgaines.runique

import android.app.Application
import com.juandgaines.auth.data.di.authModuleData
import com.juandgaines.auth.presentation.di.authViewModelModule
import com.juandgaines.core.data.di.coreDataModule
import com.juandgaines.core.database.di.databaseModule
import com.juandgaines.run.location.di.locationModule
import com.juandgaines.run.presentation.di.runPresentationModule
import com.juandgaines.runique.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp:Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@RuniqueApp)
            modules(
                authModuleData,
                authViewModelModule,
                appModule,
                coreDataModule,
                runPresentationModule,
                locationModule,
                databaseModule
            )
        }
    }
}