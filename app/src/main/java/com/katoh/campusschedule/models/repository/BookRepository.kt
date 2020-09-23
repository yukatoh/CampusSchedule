package com.katoh.campusschedule.models.repository

import android.util.Log
import com.katoh.campusschedule.models.entity.BookData
import com.katoh.campusschedule.models.net.BooksApiInterface

class BookRepository(
    private val apiInterface: BooksApiInterface
): BaseRepository() {

    /**
     * Get BookData object
     */
    suspend fun getBookData(
        queryParameters: MutableMap<String, Any>
    ): BookData? {
        // Network Result
        val result = getNetworkResult(
            call = { apiInterface.fetchBook(queryParameters) },
            error = "calling fetchBookList failed"
        )

        var output: BookData? = null

        when (result) {
            is NetworkResult.Success -> {
                output = result.output
            }
            is NetworkResult.Error ->
                Log.d("Error", "${result.exception}")
        }

        return output
    }
}
