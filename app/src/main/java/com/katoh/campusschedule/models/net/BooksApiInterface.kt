package com.katoh.campusschedule.models.net

import com.katoh.campusschedule.models.entity.BookData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface BooksApiInterface {
    @GET("/books/v1/volumes")
    suspend fun fetchBook(
        @QueryMap queryParameters: MutableMap<String, Any>
    ): Response<BookData>
}