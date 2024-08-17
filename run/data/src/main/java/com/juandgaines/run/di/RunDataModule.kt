package com.juandgaines.run.di

import com.juandgaines.run.data.CreateRunWorker
import com.juandgaines.run.data.DeleteRunWorker
import com.juandgaines.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::DeleteRunWorker)
    workerOf(::FetchRunsWorker)
}