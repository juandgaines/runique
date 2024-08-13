package com.juandgaines.core.domain.run

import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

typealias RunId = String

interface LocalRunDataSource {

    fun getRuns(): Flow<List<Run>>

    suspend fun  upsertRun(run: Run):Result<RunId, DataError.LocalError>

    suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.LocalError>

    suspend fun deleteRun(id: RunId)

    suspend fun deleteAllRuns()
}