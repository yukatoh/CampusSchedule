package com.katoh.campusschedule.models.repository

import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {

    /**
     * HTTP response
     */
    suspend fun <T : Any> getNetworkResult(
        call: suspend () -> Response<T>,
        error: String
    ): NetworkResult<T> {
        val response = call.invoke()
        return if (response.isSuccessful)
            NetworkResult.Success(response.body()!!)
        else
            NetworkResult.Error(IOException(error))
    }

}