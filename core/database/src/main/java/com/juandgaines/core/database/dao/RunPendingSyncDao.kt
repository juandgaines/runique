package com.juandgaines.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.juandgaines.core.database.entity.DeletedRunSyncEntity
import com.juandgaines.core.database.entity.RunPendingSyncEntity

@Dao
interface RunPendingSyncDao {
    @Query("SELECT * FROM RunPendingSyncEntity WHERE userId = :userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    @Query("SELECT * FROM RunPendingSyncEntity WHERE  runId = :runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    @Upsert
    suspend fun upsertRunPendingSyncEntity(runPendingSyncEntity: RunPendingSyncEntity)

    @Query("DELETE FROM RunPendingSyncEntity WHERE runId = :runId")
    suspend fun deleteRunPendingSyncEntity(runId: String)

    //Deleted Runs
    @Query("SELECT * FROM DeletedRunSyncEntity WHERE userId = :userId")
    suspend fun getAllDeletedRunSyncEntities(userId: String): List<DeletedRunSyncEntity>

    @Upsert
    suspend fun upsertDeletedRunSyncEntity(deletedRunSyncEntity: DeletedRunSyncEntity)

    @Query("DELETE FROM DeletedRunSyncEntity WHERE runId = :runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)
}