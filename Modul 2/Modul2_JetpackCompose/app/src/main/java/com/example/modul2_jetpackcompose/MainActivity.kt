package com.example.modul2_jetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipCalculatorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculatorScreen() {
    var billInput by remember { mutableStateOf("") }
    var tipPercentage by remember { mutableStateOf(15.0) }
    var roundUp by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val amount = billInput.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(amount, tipPercentage, roundUp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Calculate Tip",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
        )

        TextField(
            value = billInput,
            onValueChange = { billInput = it },
            label = { Text("Bill Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = "${tipPercentage.toInt()}%",
                onValueChange = {},
                label = { Text("Tip Percentage") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf(15.0, 18.0, 20.0).forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text("${selectionOption.toInt()}%") },
                        onClick = {
                            tipPercentage = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Round up tip?")
            Switch(
                checked = roundUp,
                onCheckedChange = { roundUp = it }
            )
        }

        Text(
            text = "Tip Amount: $tip",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

private fun calculateTip(amount: Double, tipPercent: Double, roundUp: Boolean): String {
    var tip = amount * (tipPercent / 100)
    if (roundUp) {
        tip = ceil(tip)
    }
    return NumberFormat.getCurrencyInstance().format(tip)
}