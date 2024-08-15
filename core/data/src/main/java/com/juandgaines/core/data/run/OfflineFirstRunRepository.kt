package com.juandgaines.core.data.run

import com.juandgaines.core.domain.run.LocalRunDataSource
import com.juandgaines.core.domain.run.RemoteRunDataSource
import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.domain.run.RunId
import com.juandgaines.core.domain.run.RunRepository
import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.EmptyResult
import com.juandgaines.core.domain.util.Result
import com.juandgaines.core.domain.util.Result.Error
import com.juandgaines.core.domain.util.Result.Success
import com.juandgaines.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow

class OfflineFirstRunRepository(
    private val localRunRepository: LocalRunDataSource,
    private val remoteRunRepository: RemoteRunDataSource,
    private val applicationScope: CoroutineScope
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
        val remoteResult = applicationScope.async {
            remoteRunRepository.deleteRun(id)
        }.await()
    }
}