package com.example.modul3_xml

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.modul3_xml.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MovieViewModel by viewModels {
        MovieViewModelFactory("Katalog Film Modul 4")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val onDetailAction: (Movie) -> Unit = { movie -> viewModel.onDetailClicked(movie) }
        val onImdbAction: (String) -> Unit = { url -> viewModel.onImdbClicked(url) }

        binding.rvFeaturedMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMovies.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            viewModel.movieListState.collect { movies ->
                if (movies.isNotEmpty()) {
                    binding.rvFeaturedMovies.adapter = MovieAdapter(movies, onDetailAction, onImdbAction)
                    binding.rvMovies.adapter = MovieAdapter(movies, onDetailAction, onImdbAction)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.clickEventState.collect { event ->
                when (event) {
                    is MovieViewModel.ClickEvent.NavigateToDetail -> {
                        val movie = event.movie
                        val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
                            putExtra("MOVIE_TITLE", movie.title)
                            putExtra("MOVIE_YEAR", movie.year)
                            putExtra("MOVIE_PLOT", movie.plot)
                            putExtra("MOVIE_POSTER", movie.posterRes)
                        }
                        startActivity(intent)
                        viewModel.resetClickEvent()
                    }
                    is MovieViewModel.ClickEvent.OpenImdbLink -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                        startActivity(intent)
                        viewModel.resetClickEvent()
                    }
                    null -> {}
                }
            }
        }
    }
}