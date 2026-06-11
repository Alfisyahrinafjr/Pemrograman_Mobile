package com.example.modul1_jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceApp()
        }
    }
}

@Composable
fun DiceApp() {
    var dice1 by remember { mutableStateOf(0) }
    var dice2 by remember { mutableStateOf(0) }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Row {
            Image(
                painter = painterResource(id = getDiceImage(dice1)),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = getDiceImage(dice2)),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            dice1 = (1..6).random()
            dice2 = (1..6).random()

            result = if (dice1 == dice2) {
                "Selamat, anda dapat dadu double!"
            } else {
                "Anda belum beruntung!"
            }
        }) {
            Text("Roll")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(result)
    }
}

fun getDiceImage(value: Int): Int {
    return when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_0
    }
}
