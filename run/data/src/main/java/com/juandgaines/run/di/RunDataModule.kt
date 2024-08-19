package com.juandgaines.run.di

import com.juandgaines.core.domain.run.SyncRunScheduler
import com.juandgaines.run.data.CreateRunWorker
import com.juandgaines.run.data.DeleteRunWorker
import com.juandgaines.run.data.FetchRunsWorker
import com.juandgaines.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)
    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}