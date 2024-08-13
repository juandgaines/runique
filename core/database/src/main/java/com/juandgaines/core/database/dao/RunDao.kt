package com.juandgaines.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.juandgaines.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Upsert
    suspend fun upsert(run: RunEntity)

    @Upsert
    suspend fun upsert(runs: List<RunEntity>)

    @Query("SELECT * FROM RunEntity ORDER BY dateTimeUtc DESC")
    fun getAllRuns():Flow<List<RunEntity>>

    @Query("SELECT * FROM RunEntity WHERE id = :id")
    suspend fun deleteRun(id: String)

    @Query("DELETE FROM RunEntity")
    suspend fun deleteAllRuns()
}
