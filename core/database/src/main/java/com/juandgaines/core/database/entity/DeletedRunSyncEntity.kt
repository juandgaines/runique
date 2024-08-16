package com.juandgaines.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.juandgaines.core.domain.run.RunId

@Entity
data class DeletedRunSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String,
)