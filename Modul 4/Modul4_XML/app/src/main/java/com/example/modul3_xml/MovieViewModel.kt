package com.example.modul3_xml

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class MovieViewModel(private val pageTitleParam: String) : ViewModel() {

    private val _movieListState = MutableStateFlow<List<Movie>>(emptyList())
    val movieListState: StateFlow<List<Movie>> = _movieListState

    private val _clickEventState = MutableStateFlow<ClickEvent?>(null)
    val clickEventState: StateFlow<ClickEvent?> = _clickEventState

    sealed class ClickEvent {
        data class NavigateToDetail(val movie: Movie) : ClickEvent()
        data class OpenImdbLink(val url: String) : ClickEvent()
    }

    init {
        loadInitialMovies()
    }

    private fun loadInitialMovies() {
        val rawMovies = listOf(
            Movie(
                1,
                "Pengabdi Setan 2: Communion",
                "2022",
                "Sekelompok keluarga yang tinggal di rumah susun kembali diteror oleh kekuatan gaib setelah selamat dari kejadian mengerikan di masa lalu.",
                R.drawable.pengabdi_setan_2,
                "https://www.imdb.com/title/tt16915972/"
            ),
            Movie(
                2,
                "Siksa Kubur",
                "2024",
                "Seorang wanita berusaha membuktikan ada tidaknya siksa kubur setelah kehilangan kedua orang tuanya dalam peristiwa tragis.",
                R.drawable.siksa_kubur,
                "https://www.imdb.com/title/tt27004148/"
            ),
            Movie(
                3,
                "Pengepungan di Bukit Duri",
                "2025",
                "Seorang guru menghadapi situasi berbahaya ketika terjebak bersama murid-murid bermasalah di sebuah sekolah yang penuh konflik.",
                R.drawable.pengepungan_bukit_duri,
                "https://www.imdb.com/title/tt33479839/"
            ),
            Movie(
                4,
                "Ghost in the Cell",
                "2026",
                "Seorang narapidana mulai mengalami kejadian mistis di dalam sel penjara yang menyimpan rahasia kelam dari masa lalu.",
                R.drawable.ghost_in_cell,
                "https://www.imdb.com/title/tt9000310/"
            ),
            Movie(
                5,
                "Perempuan Tanah Jahanam",
                "2019",
                "Seorang perempuan kembali ke desa leluhurnya untuk mencari warisan, namun justru menemukan teror dan kutukan mengerikan.",
                R.drawable.impetigore,
                "https://www.imdb.com/title/tt9000302/"
            )
        )

        _movieListState.value = rawMovies

        Timber.d("Modul 4 [Timber]: Data film berhasil masuk ke list. Parameter Halaman: $pageTitleParam")
    }

    fun onDetailClicked(movie: Movie) {
        _clickEventState.value = ClickEvent.NavigateToDetail(movie)

        Timber.d("Modul 4 [Timber]: Tombol Detail diklik! Data terpilih -> Judul: ${movie.title}")
    }

    fun onImdbClicked(url: String) {
        _clickEventState.value = ClickEvent.OpenImdbLink(url)

        Timber.d("Modul 4 [Timber]: Tombol Intent IMDB diklik! Menuju ke: $url")
    }

    fun resetClickEvent() {
        _clickEventState.value = null
    }
}

class MovieViewModelFactory(private val titleParam: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovieViewModel(titleParam) as T
        }
        throw IllegalArgumentException("Kelas ViewModel tidak dikenal")
    }
}