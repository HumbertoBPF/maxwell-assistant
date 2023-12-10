package com.example.maxwell.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun parseDate(dueDate: String): Date? {
    val sdf = SimpleDateFormat("MM-dd-yyyy", Locale.US)
    sdf.isLenient = false
    return sdf.parse(dueDate)
}