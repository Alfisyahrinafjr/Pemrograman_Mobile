package com.example.modul5_jetpackcompose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.modul5_jetpackcompose.data.ApiResponse
import com.example.modul5_jetpackcompose.data.AppDatabase
import com.example.modul5_jetpackcompose.data.MovieEntity
import com.example.modul5_jetpackcompose.data.MovieRepository
import com.example.modul5_jetpackcompose.data.NetworkClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = MovieRepository(NetworkClient.apiService, database.movieDao())
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onSurface = Color.White
            )) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val movieViewModel: MovieViewModel = viewModel(
                        factory = MovieViewModelFactory(repository, sharedPreferences)
                    )
                    AppNavigation(movieViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MovieViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val movieState by viewModel.movieState.collectAsState()
    val clickEvent by viewModel.clickEventState.collectAsState()

    LaunchedEffect(clickEvent) {
        when (val event = clickEvent) {
            is MovieViewModel.ClickEvent.NavigateToDetail -> {
                navController.navigate("detail/${event.movie.id}")
                viewModel.resetClickEvent()
            }
            is MovieViewModel.ClickEvent.OpenImdbLink -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                context.startActivity(intent)
                viewModel.resetClickEvent()
            }
            null -> {}
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            when (val state = movieState) {
                is ApiResponse.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF98AFE9))
                    }
                }
                is ApiResponse.Success -> {
                    HomeScreen(
                        movies = state.data,
                        onDetailClick = { movie -> viewModel.onDetailClicked(movie) },
                        onImdbClick = { id -> viewModel.onImdbClicked(id) }
                    )
                }
                is ApiResponse.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${state.errorMessage}", color = Color.Red)
                    }
                }
            }
        }
        composable(
            "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("movieId")

            // Cari data film dari state success saat ini
            val currentMovies = (movieState as? ApiResponse.Success)?.data ?: emptyList()
            val movie = currentMovies.find { it.id == id }

            DetailScreen(movie) { navController.popBackStack() }
        }
    }
}

@Composable
fun HomeScreen(movies: List<MovieEntity>, onDetailClick: (MovieEntity) -> Unit, onImdbClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Featured Movies", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(movies.take(5)) { movie ->
                        FeaturedMovieItem(movie, onDetailClick)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("All Movies From TMDB", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        items(movies) { movie ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                MovieItem(movie, onDetailClick, onImdbClick)
            }
        }
    }
}

@Composable
fun MovieItem(movie: MovieEntity, onDetailClick: (MovieEntity) -> Unit, onImdbClick: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    contentDescription = movie.title,
                    modifier = Modifier.size(width = 90.dp, height = 130.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(movie.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(movie.releaseDate.take(4), fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(movie.overview, fontSize = 12.sp, color = Color.LightGray, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                Button(onClick = { onImdbClick(movie.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98AFE9))) {
                    Text("TMDB Web", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onDetailClick(movie) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98AFE9))) {
                    Text("Detail", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FeaturedMovieItem(movie: MovieEntity, onDetailClick: (MovieEntity) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        modifier = Modifier.width(280.dp).clickable { onDetailClick(movie) }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(movie.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(movie.overview, fontSize = 11.sp, color = Color.LightGray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun DetailScreen(movie: MovieEntity?, onBack: () -> Unit) {
    Scaffold(containerColor = Color(0xFF121212)) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            movie?.let {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${it.posterPath}",
                    contentDescription = it.title,
                    modifier = Modifier.size(width = 200.dp, height = 300.dp).clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(it.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 20.dp))
                Text("Rilis: ${it.releaseDate}", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text(it.overview, color = Color.LightGray, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98AFE9))) {
                Text("Kembali", color = Color.Black)
            }
        }
    }
}