package com.juandgaines.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.juandgaines.core.database.dao.RunDao
import com.juandgaines.core.database.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1
)
abstract class RunDataBase :RoomDatabase() {
    abstract fun runDao(): RunDao
}