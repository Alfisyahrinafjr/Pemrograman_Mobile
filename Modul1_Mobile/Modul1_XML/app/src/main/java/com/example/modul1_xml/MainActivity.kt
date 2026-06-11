package com.example.modul1_xml

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dice1: ImageView
    private lateinit var dice2: ImageView
    private lateinit var resultText: TextView
    private lateinit var btnRoll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dice1 = findViewById(R.id.dice1)
        dice2 = findViewById(R.id.dice2)
        resultText = findViewById(R.id.tvResult)
        btnRoll = findViewById(R.id.btnRoll)

        btnRoll.setOnClickListener {
            rollDice()
        }
    }

    private fun rollDice() {
        val d1 = (1..6).random()
        val d2 = (1..6).random()

        dice1.setImageResource(getDiceImage(d1))
        dice2.setImageResource(getDiceImage(d2))

        if (d1 == d2) {
            resultText.text = "Selamat, anda dapat dadu double!"
        } else {
            resultText.text = "Anda belum beruntung!"
        }
    }

    private fun getDiceImage(value: Int): Int {
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
}
