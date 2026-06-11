package com.example.modul5_jetpackcompose.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class MovieRepository(
    private val apiService: ApiService,
    private val movieDao: MovieDao
) {
    private val apiToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmNGU2ODk4OThhZmVkMWRjMTQ3YjllOWMzY2YwNzJiYyIsIm5iZiI6MTc4MTA5Mzc1MC4zODQsInN1YiI6IjZhMjk1NTc2OTgwODYyZjI2NzkzMjRmZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.fL_yIlp_Rp2FNf6F5L08aQinPe5jNCuaOxXsMDtev64"

    fun getPopularMovies(): Flow<ApiResponse<List<MovieEntity>>> = flow {
        emit(ApiResponse.Loading)

        try {
            // Tarik data internet dari API
            Timber.d("Modul 5 Log: Mengambil data film terbaru dari TMDB API...")
            val response = apiService.getPopularMovies(bearerToken = apiToken)
            val remoteMovies = response.results

            val entityList = remoteMovies.map { remote ->
                MovieEntity(
                    id = remote.id,
                    title = remote.title,
                    releaseDate = remote.releaseDate ?: "Unknown",
                    overview = remote.overview,
                    posterPath = remote.posterPath ?: ""
                )
            }

            movieDao.deleteAllMovies()
            movieDao.insertMovies(entityList)
            Timber.d("Modul 5 Log: Cache Room berhasil diperbarui dengan data TMDB!")
        } catch (e: Exception) {
            Timber.e("Modul 5 Log: Gagal koneksi ke API (${e.localizedMessage}), beralih ke offline mode.")
        }

        movieDao.getAllMovies().map { ApiResponse.Success(it) }.collect {
            emit(it)
        }
    }
}