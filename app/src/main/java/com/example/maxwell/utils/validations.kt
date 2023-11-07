package com.example.maxwell.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

fun String.hasValidDateFormat(): Boolean {
    val sdf = SimpleDateFormat("MM-dd-yyyy", Locale.US)
    sdf.isLenient = false
    return try {
        sdf.parse(this)
        true
    } catch (e: ParseException) {
        false
    }
}