package com.juandgaines.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.juandgaines.core.database.dao.AnalyticsDao
import com.juandgaines.core.database.dao.RunDao
import com.juandgaines.core.database.dao.RunPendingSyncDao
import com.juandgaines.core.database.entity.DeletedRunSyncEntity
import com.juandgaines.core.database.entity.RunEntity
import com.juandgaines.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 1
)
abstract class RunDataBase :RoomDatabase() {
    abstract fun runDao(): RunDao
    abstract fun runPendingSyncDao(): RunPendingSyncDao
    abstract fun analyticsDao(): AnalyticsDao
}