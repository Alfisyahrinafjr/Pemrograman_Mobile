package com.example.modul3_jetpackcompose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
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
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                background = Color(0xFF121212),
                surface = Color(0xFF1E1E1E),
                onSurface = Color.White
            )) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val movieViewModel: MovieViewModel = viewModel(
                        factory = MovieViewModelFactory("Katalog Film Jetpack Compose")
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
    val movies by viewModel.movieListState.collectAsState()
    val clickEvent by viewModel.clickEventState.collectAsState()

    LaunchedEffect(clickEvent) {
        when (val event = clickEvent) {
            is MovieViewModel.ClickEvent.NavigateToDetail -> {
                val movie = event.movie
                navController.navigate("detail/${movie.id}")
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
            HomeScreen(
                movies = movies,
                onDetailClick = { movie -> viewModel.onDetailClicked(movie) },
                onImdbClick = { url -> viewModel.onImdbClicked(url) }
            )
        }
        composable(
            "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("movieId")
            val movie = movies.find { it.id == id }
            DetailScreen(movie) { navController.popBackStack() }
        }
    }
}

@Composable
fun HomeScreen(movies: List<Movie>, onDetailClick: (Movie) -> Unit, onImdbClick: (String) -> Unit) {
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
                    items(movies.take(3)) { movie ->
                        FeaturedMovieItem(movie)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("All Movies", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
fun MovieItem(movie: Movie, onDetailClick: (Movie) -> Unit, onImdbClick: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = movie.posterRes),
                    contentDescription = null,
                    modifier = Modifier.size(width = 90.dp, height = 130.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(movie.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, modifier = Modifier.weight(1f))
                        Text(movie.year, fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(movie.plot, fontSize = 12.sp, color = Color.LightGray, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.End) {
                Button(onClick = { onImdbClick(movie.imdbUrl) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98AFE9))) {
                    Text("IMDB", color = Color.Black, fontWeight = FontWeight.Bold)
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
fun FeaturedMovieItem(movie: Movie) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        modifier = Modifier.width(280.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = movie.posterRes),
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(movie.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White, maxLines = 1)
                Text(movie.plot, fontSize = 11.sp, color = Color.LightGray, maxLines = 2)
            }
        }
    }
}

@Composable
fun DetailScreen(movie: Movie?, onBack: () -> Unit) {
    Scaffold(containerColor = Color(0xFF121212)) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            movie?.let {
                Image(
                    painter = painterResource(id = it.posterRes),
                    contentDescription = null,
                    modifier = Modifier.size(width = 200.dp, height = 300.dp).clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(it.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 20.dp))
                Text("Tahun: ${it.year}", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Text(it.plot, color = Color.LightGray, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98AFE9))) {
                Text("Kembali", color = Color.Black)
            }
        }
    }
}