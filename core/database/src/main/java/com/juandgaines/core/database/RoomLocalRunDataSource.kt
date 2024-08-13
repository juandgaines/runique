package com.juandgaines.core.database

import android.database.sqlite.SQLiteFullException
import com.juandgaines.core.database.dao.RunDao
import com.juandgaines.core.database.mappers.toRun
import com.juandgaines.core.database.mappers.toRunEntity
import com.juandgaines.core.domain.run.LocalRunDataSource
import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.domain.run.RunId
import com.juandgaines.core.domain.util.DataError.LocalError
import com.juandgaines.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalRunDataSource(
    private val runDao: RunDao
): LocalRunDataSource {
    override fun getRuns(): Flow<List<Run>> {
        return runDao.getAllRuns().map {runEntities ->
            runEntities.map { it.toRun()}
        }
    }

    override suspend fun upsertRun(run: Run): Result<RunId, LocalError> {
        return try {
            val entity = run.toRunEntity()
            runDao.upsert(run.toRunEntity())
            Result.Success(entity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(LocalError.DISK_FULL)
        }
    }

    override suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, LocalError> {
        return try {
            val entities = runs.map { it.toRunEntity() }
            runDao.upsert(entities)
            Result.Success(entities.map { it.id })
        } catch (e: SQLiteFullException) {
            Result.Error(LocalError.DISK_FULL)
        }
    }

    override suspend fun deleteRun(id: RunId) {
        runDao.deleteRun(id)
    }

    override suspend fun deleteAllRuns() {
        runDao.deleteAllRuns()
    }
}