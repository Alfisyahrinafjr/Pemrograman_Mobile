package com.example.modul5_jetpackcompose

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.modul5_jetpackcompose.data.ApiResponse
import com.example.modul5_jetpackcompose.data.MovieEntity
import com.example.modul5_jetpackcompose.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MovieViewModel(
    private val repository: MovieRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _movieState = MutableStateFlow<ApiResponse<List<MovieEntity>>>(ApiResponse.Loading)
    val movieState: StateFlow<ApiResponse<List<MovieEntity>>> = _movieState

    private val _clickEventState = MutableStateFlow<ClickEvent?>(null)
    val clickEventState: StateFlow<ClickEvent?> = _clickEventState

    sealed class ClickEvent {
        data class NavigateToDetail(val movie: MovieEntity) : ClickEvent()
        data class OpenImdbLink(val url: String) : ClickEvent()
    }

    init {
        fetchMovies()
        saveAppThemePreference()
    }

    fun fetchMovies() {
        viewModelScope.launch {
            repository.getPopularMovies().collect { response ->
                _movieState.value = response
            }
        }
    }

    private fun saveAppThemePreference() {
        sharedPreferences.edit().putString("app_theme", "DARK_MODE").apply()
        val savedTheme = sharedPreferences.getString("app_theme", "DEFAULT")
        Timber.d("Modul 5 Log (SharedPreferences): Berhasil menyimpan pengaturan tema -> $savedTheme")
    }

    fun onDetailClicked(movie: MovieEntity) {
        _clickEventState.value = ClickEvent.NavigateToDetail(movie)
        Timber.d("Modul 5 Log: Detail diklik untuk film: ${movie.title}")
    }

    fun onImdbClicked(movieId: Int) {
        val tmdbUrl = "https://www.themoviedb.org/movie/$movieId"
        _clickEventState.value = ClickEvent.OpenImdbLink(tmdbUrl)
        Timber.d("Modul 5 Log: Intent eksternal dipicu menuju web TMDB: $tmdbUrl")
    }

    fun resetClickEvent() {
        _clickEventState.value = null
    }
}

class MovieViewModelFactory(
    private val repository: MovieRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(repository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Kelas ViewModel tidak dikenal")
    }
}