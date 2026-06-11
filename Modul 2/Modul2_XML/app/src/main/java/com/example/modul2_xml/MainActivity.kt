package com.example.modul2_xml

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private lateinit var etBillAmount: TextInputEditText
    private lateinit var actvTipPercentage: AutoCompleteTextView
    private lateinit var switchRoundUp: SwitchMaterial
    private lateinit var tvTipAmount: TextView

    private var selectedTipPercent = 15.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBillAmount = findViewById(R.id.etBillAmount)
        actvTipPercentage = findViewById(R.id.actvTipPercentage)
        switchRoundUp = findViewById(R.id.switchRoundUp)
        tvTipAmount = findViewById(R.id.tvTipAmount)

        val options = listOf("15%", "18%", "20%")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        actvTipPercentage.setAdapter(adapter)

        actvTipPercentage.setOnItemClickListener { _, _, position, _ ->
            selectedTipPercent = when (position) {
                0 -> 15.0
                1 -> 18.0
                2 -> 20.0
                else -> 15.0
            }
            calculateAndShowTip()
        }

        etBillAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateAndShowTip()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        switchRoundUp.setOnCheckedChangeListener { _, _ ->
            calculateAndShowTip()
        }
    }

    private fun calculateAndShowTip() {
        val amountString = etBillAmount.text.toString()
        val amount = amountString.toDoubleOrNull() ?: 0.0

        var tip = amount * (selectedTipPercent / 100)

        if (switchRoundUp.isChecked) {
            tip = ceil(tip)
        }

        val formattedTip = NumberFormat.getCurrencyInstance().format(tip)
        tvTipAmount.text = "Tip Amount: $formattedTip"
    }
}