package com.example.modul3_xml

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.modul3_xml.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("MOVIE_TITLE")
        val year = intent.getStringExtra("MOVIE_YEAR")
        val plot = intent.getStringExtra("MOVIE_PLOT")
        val poster = intent.getIntExtra("MOVIE_POSTER", 0)

        binding.apply {
            txtDetailTitle.text = title
            txtDetailYear.text = "Tahun: $year"
            txtDetailPlot.text = plot
            imgDetailPoster.setImageResource(poster)

            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}