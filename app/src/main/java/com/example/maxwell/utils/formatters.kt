package com.example.maxwell.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun padWithZeros(number: Int): String {
    if (number < 10) {
        return "0$number"
    }

    return "$number"
}

fun formatDateForInput(date: Date): String {
    val simpleDateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)
    return simpleDateFormat.format(date)
}

fun formatDatePretty(date: Date): String {
    val simpleDateFormat = SimpleDateFormat("LLLL dd, yyyy", Locale.US)
    return simpleDateFormat.format(date)
}