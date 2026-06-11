package com.example.modul3_xml

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modul3_xml.databinding.ActivityMainBinding

data class Movie(
    val id: Int,
    val title: String,
    val year: String,
    val plot: String,
    val posterRes: Int,
    val imdbUrl: String
)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieList = listOf(
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

        val onDetailClickAction: (Movie) -> Unit = { movie ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("MOVIE_TITLE", movie.title)
                putExtra("MOVIE_YEAR", movie.year)
                putExtra("MOVIE_PLOT", movie.plot)
                putExtra("MOVIE_POSTER", movie.posterRes)
            }
            startActivity(intent)
        }

        val onImdbClickAction: (String) -> Unit = { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        val featuredAdapter = MovieAdapter(movieList, onDetailClickAction, onImdbClickAction)
        binding.rvFeaturedMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = featuredAdapter
        }

        val allMoviesAdapter = MovieAdapter(movieList, onDetailClickAction, onImdbClickAction)
        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = allMoviesAdapter
        }
    }
}