package com.example.modul5_jetpackcompose.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDBResponse(
    @SerialName("results")
    val results: List<RemoteMovie>
)

@Serializable
data class RemoteMovie(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("release_date")
    val releaseDate: String? = "",
    @SerialName("overview")
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String? = ""
)