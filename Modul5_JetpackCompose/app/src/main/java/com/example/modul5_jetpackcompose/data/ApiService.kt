package com.example.modul5_jetpackcompose.data

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header

interface ApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Header("Authorization") bearerToken: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): TMDBResponse
}