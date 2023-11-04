package com.example.maxwell.utils

fun padWithZeros(number: Int): String {
    if (number < 10) {
        return "0$number"
    }

    return "$number"
}