package com.juandgaines.run.data

import android.content.Context
import androidx.work.BackoffPolicy.EXPONENTIAL
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType.CONNECTED
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.juandgaines.core.database.dao.RunPendingSyncDao
import com.juandgaines.core.database.entity.DeletedRunSyncEntity
import com.juandgaines.core.database.entity.RunPendingSyncEntity
import com.juandgaines.core.database.mappers.toRunEntity
import com.juandgaines.core.domain.SessionStorage
import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.domain.run.SyncRunScheduler
import com.juandgaines.core.domain.run.SyncRunScheduler.SyncType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    private val context: Context,
    private val pendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(syncType: SyncType) {
        when(syncType){
            is SyncType.FetchRuns -> scheduleFetchRunWorker(syncType.interval)
            is SyncType.DeleteRun -> scheduleDeleteRunWorker(syncType.runId)
            is SyncType.CreateRun -> scheduleCreateRunWorker(syncType.run,syncType.mapPictureBytes)
        }
    }

    private suspend fun scheduleDeleteRunWorker(runId: String) {
        val userId = sessionStorage.get()?.userId ?: return
        val pendingRun = DeletedRunSyncEntity(
            runId = runId,
            userId = userId
        )
        pendingSyncDao.upsertDeletedRunSyncEntity(pendingRun)

        val workRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
            .addTag("delete_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, pendingRun.runId)
                    .build()
            )
            .build()
        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun scheduleCreateRunWorker(run: Run, mapPictureBytes: ByteArray) {
        val userId = sessionStorage.get()?.userId ?: return
        val pendingRun = RunPendingSyncEntity(
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userId
        )
        pendingSyncDao.upsertRunPendingSyncEntity(pendingRun)

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag("create_work")
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateRunWorker.RUN_ID,pendingRun.runId)
                    .build()
            )
            .build()
        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()

    }

    private suspend fun scheduleFetchRunWorker(interval:Duration){
        val isSyncScheduled = withContext(Dispatchers.IO){
            workManager.getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }
        if(isSyncScheduled){
            return
        }

        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(
            repeatInterval = interval.toJavaDuration(),
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(
                duration = 30,
                timeUnit = TimeUnit.MINUTES
            )
            .addTag("sync_work")
            .build()

        workManager.enqueue(workRequest).await()
    }

    override suspend fun cancelSync() {
        WorkManager
            .getInstance(context)
            .cancelAllWork()
            .await()
    }
}