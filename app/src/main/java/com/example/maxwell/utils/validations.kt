package com.example.maxwell.utils

import java.text.ParseException

fun String.hasValidDateFormat(): Boolean = try {
    parseDate(this)
    true
} catch (e: ParseException) {
    false
}