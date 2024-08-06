package com.juandgaines.runique

import android.app.Application
import com.juandgaines.auth.data.di.authModuleData
import com.juandgaines.auth.presentation.di.authViewModelModule
import com.juandgaines.core.data.di.coreDataModule
import com.juandgaines.run.presentation.di.runViewModelModule
import com.juandgaines.runique.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RuniqueApp:Application() {
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
                runViewModelModule
            )
        }
    }
}