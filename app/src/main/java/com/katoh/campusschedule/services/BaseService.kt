package com.katoh.campusschedule.services

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

abstract class BaseService {

    companion object {
        // Use Google API
        private const val BASE_URL = "https://www.googleapis.com"
    }

    /**
     * API client
     */
    private val client: OkHttpClient
        get() = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    /**
     * API connection
     */
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(
                        KotlinJsonAdapterFactory()
                    ).build()
                )
            )
            .build()

}