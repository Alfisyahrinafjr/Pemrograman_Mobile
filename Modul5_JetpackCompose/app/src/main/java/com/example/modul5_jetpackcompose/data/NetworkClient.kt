package com.example.modul5_jetpackcompose.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object NetworkClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}