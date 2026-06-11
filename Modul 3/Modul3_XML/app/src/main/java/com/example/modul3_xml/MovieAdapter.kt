package com.example.modul3_xml

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modul3_xml.databinding.ItemMovieBinding

class MovieAdapter(
    private val movies: List<Movie>,
    private val onDetailClick: (Movie) -> Unit,
    private val onImdbClick: (String) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.binding.apply {
            txtTitle.text = movie.title
            txtYear.text = movie.year
            txtPlot.text = movie.plot

            imgPoster.setImageResource(movie.posterRes)

            btnDetail.setOnClickListener {
                onDetailClick(movie)
            }

            btnImdb.setOnClickListener {
                onImdbClick(movie.imdbUrl)
            }
        }
    }

    override fun getItemCount(): Int = movies.size
}