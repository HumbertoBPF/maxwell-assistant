package com.example.maxwell.models

import com.example.maxwell.R
import java.text.NumberFormat
import java.util.Locale

enum class Currency(
    val text: String,
    val stringResource: Int,
    val formatter: NumberFormat
) {
    BRL(
        "BRL",
        R.string.brl_radio_button,
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    ),
    EUR(
        "EUR",
        R.string.euro_radio_button,
        NumberFormat.getCurrencyInstance(Locale.FRANCE)
    )
}