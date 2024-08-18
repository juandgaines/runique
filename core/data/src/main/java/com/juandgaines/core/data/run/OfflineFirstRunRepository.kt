package com.juandgaines.core.data.run

import com.juandgaines.core.database.dao.RunPendingSyncDao
import com.juandgaines.core.database.mappers.toRun
import com.juandgaines.core.domain.SessionStorage
import com.juandgaines.core.domain.run.LocalRunDataSource
import com.juandgaines.core.domain.run.RemoteRunDataSource
import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.domain.run.RunId
import com.juandgaines.core.domain.run.RunRepository
import com.juandgaines.core.domain.run.SyncRunScheduler
import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.EmptyResult
import com.juandgaines.core.domain.util.Result
import com.juandgaines.core.domain.util.Result.Error
import com.juandgaines.core.domain.util.Result.Success
import com.juandgaines.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val localRunRepository: LocalRunDataSource,
    private val remoteRunRepository: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val syncRunScheduler: SyncRunScheduler
) :RunRepository{
    override fun getRuns(): Flow<List<Run>> {
        return localRunRepository.getRuns()
    }
    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunRepository.getRuns()) {
            is Error -> result.asEmptyDataResult()
            is Success -> {
                applicationScope.async {
                    localRunRepository.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray,
    ): EmptyResult<DataError> {
        val localResult = localRunRepository.upsertRun(run)
        if (localResult !is Success) {
            return localResult.asEmptyDataResult()
        }
        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunRepository.postRun(
            runWithId,
            mapPicture
        )
        return when (remoteResult) {
            is Error -> {
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(
                        SyncRunScheduler.SyncType.CreateRun(
                            run = runWithId,
                            mapPictureBytes = mapPicture
                        )
                    )
                }.join()
                Result.Success(Unit)
            }
            is Success -> {
                applicationScope.async {
                    localRunRepository.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }
    override suspend fun deleteRun(id: RunId){
        val localResult = localRunRepository.deleteRun(id)

        //Edge case where the run is created in offline mode and then deleted before being synced also in offline mode
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null
        if(isPendingSync){
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }
        val remoteResult = applicationScope.async {
            remoteRunRepository.deleteRun(id)
        }.await()

        if (remoteResult is Error) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(
                    SyncRunScheduler.SyncType.DeleteRun(id)
                )
            }.join()
        }
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }
            val createJobs= createdRuns
                .await()
                .map {
                    launch {
                        val run =it.run.toRun()
                        when (remoteRunRepository.postRun(run,it.mapPictureBytes)) {
                            is Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join()
                            }
                            is Error -> Unit
                        }
                    }
                }
            val deletedJobs = deletedRuns
                .await()
                .map {
                    launch {
                        when (remoteRunRepository.deleteRun(it.runId)) {
                            is Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                            is Error -> Unit
                        }
                    }
                }
            createJobs.forEach { it.join() }
            deletedJobs.forEach { it.join() }
        }
    }
}