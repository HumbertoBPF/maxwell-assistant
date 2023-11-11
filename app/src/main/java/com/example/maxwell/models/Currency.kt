package com.example.maxwell.models

import java.text.NumberFormat
import java.util.Locale

enum class Currency(val text: String, val formatter: NumberFormat) {
    BRL("BRL", NumberFormat.getCurrencyInstance(Locale("pt", "BR"))),
    EUR("EUR", NumberFormat.getCurrencyInstance(Locale.FRANCE))
}