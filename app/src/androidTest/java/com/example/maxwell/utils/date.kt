package com.example.maxwell.utils

import java.util.Calendar
import java.util.Calendar.MILLISECOND

fun getCalendar(year: Int, month: Int, day: Int): Calendar {
    val calendar = Calendar.getInstance()

    calendar.set(year, month, day, 0, 0, 0)
    calendar.set(MILLISECOND, 0)
    
    return calendar
}