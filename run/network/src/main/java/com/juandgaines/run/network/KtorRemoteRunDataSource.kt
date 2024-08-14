package com.juandgaines.run.network

import com.juandgaines.core.data.networking.constructRoute
import com.juandgaines.core.data.networking.delete
import com.juandgaines.core.data.networking.get
import com.juandgaines.core.data.networking.safeCall
import com.juandgaines.core.domain.run.RemoteRunDataSource
import com.juandgaines.core.domain.run.Run
import com.juandgaines.core.domain.util.DataError.Network
import com.juandgaines.core.domain.util.EmptyResult
import com.juandgaines.core.domain.util.Result
import com.juandgaines.core.domain.util.map
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.Headers.Companion
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRemoteRunDataSource(
    private val httpClient:HttpClient
): RemoteRunDataSource {

    override suspend fun getRuns(): Result<List<Run>, Network> {
        return httpClient.get<List<RunDto>>(
            route = "/runs",
        ).map { runDtos->
            runDtos.map { it.toRun() }
        }
    }

    override suspend fun postRun(
        run: Run,
        mapPicture: ByteArray,
    ): Result<Run, Network> {
        //Multipart form data
        val createRunRequest = Json.encodeToString(run.toCreeateRunRequest())
        val result = safeCall<RunDto>{
            httpClient.submitFormWithBinaryData(
                url = constructRoute("/run"),
                formData = formData {
                    append("MAP_PICTURE",mapPicture, Headers.build {
                        append(HttpHeaders.ContentType,"image/jpeg")
                        append(HttpHeaders.ContentDisposition,"filename=mappicture.jpg")
                    })
                    append("RUN_DATA", createRunRequest, Headers.build {
                        append(HttpHeaders.ContentType,"text/plain")
                        append(HttpHeaders.ContentDisposition,"form-data; name=\"RUN_DATA\"")
                    })
                }
            ){
                method = HttpMethod.Post
            }
        }
        return result.map {
            it.toRun()
        }
    }

    override suspend fun deleteRun(id: String): EmptyResult<Network> {
        return httpClient.delete(
            route = "/run",
            queryParameter = mapOf("id" to id)
        )
    }
}