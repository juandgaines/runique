package com.juandgaines.run.data

import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result
import com.juandgaines.core.domain.util.DataError
import com.juandgaines.core.domain.util.DataError.LocalError.DISK_FULL
import com.juandgaines.core.domain.util.DataError.LocalError.FILE_ERROR
import com.juandgaines.core.domain.util.DataError.LocalError.UNKNOWN
import com.juandgaines.core.domain.util.DataError.Network
import com.juandgaines.core.domain.util.DataError.Network.CONFLICT
import com.juandgaines.core.domain.util.DataError.Network.NO_INTERNET
import com.juandgaines.core.domain.util.DataError.Network.PAYLOAD_TOO_LARGE
import com.juandgaines.core.domain.util.DataError.Network.REQUEST_TIMEOUT
import com.juandgaines.core.domain.util.DataError.Network.SERIALIZATION
import com.juandgaines.core.domain.util.DataError.Network.SERVER_ERROR
import com.juandgaines.core.domain.util.DataError.Network.TOO_MANY_REQUESTS
import com.juandgaines.core.domain.util.DataError.Network.UNAUTHORIZED

fun DataError.toWorkerResult(): ListenableWorker.Result{
    return when(this){
        DISK_FULL -> Result.failure()
        FILE_ERROR -> Result.failure()
        UNKNOWN -> Result.failure()
        REQUEST_TIMEOUT -> Result.retry()
        UNAUTHORIZED -> Result.retry()
        CONFLICT -> Result.retry()
        TOO_MANY_REQUESTS -> Result.retry()
        NO_INTERNET -> Result.retry()
        PAYLOAD_TOO_LARGE -> Result.failure()
        SERVER_ERROR -> Result.retry()
        SERIALIZATION -> Result.failure()
        Network.UNKNOWN -> Result.failure()
    }
}