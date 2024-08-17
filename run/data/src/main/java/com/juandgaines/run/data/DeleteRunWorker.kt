package com.juandgaines.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.juandgaines.core.database.dao.RunPendingSyncDao
import com.juandgaines.core.domain.run.RemoteRunDataSource

class DeleteRunWorker(
    private val context: Context,
    private val params: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
): CoroutineWorker(context,params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount > 5) {
            return Result.failure()
        }

        val runId = params.inputData.getString(RUN_ID) ?: return Result.failure()
        return when (val result = remoteRunDataSource.deleteRun(runId)) {
            is com.juandgaines.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }
            is com.juandgaines.core.domain.util.Result.Success -> {
                pendingSyncDao.deleteRunPendingSyncEntity(runId)
                Result.success()
            }
        }
    }

    companion object {
        const val RUN_ID = "runId"
    }
}