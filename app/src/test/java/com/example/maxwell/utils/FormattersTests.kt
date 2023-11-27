package com.example.maxwell.utils

import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.DECEMBER
import java.util.Calendar.JANUARY
import java.util.Calendar.MONTH
import java.util.Calendar.NOVEMBER
import java.util.Calendar.YEAR

class FormattersTests {
    @Test
    fun `should format date to pretty string`() {
        val calendar = Calendar.getInstance()

        calendar.set(MONTH, NOVEMBER)
        calendar.set(DAY_OF_MONTH, 20)
        calendar.set(YEAR, 1695)

        val formattedDate = formatDatePretty(calendar.time)

        formattedDate shouldBeEqualTo "November 20, 1695"
    }

    @Test
    fun `should pad zeros of the day when formatting date to pretty string`() {
        val calendar = Calendar.getInstance()

        calendar.set(MONTH, JANUARY)
        calendar.set(DAY_OF_MONTH, 1)
        calendar.set(YEAR, 2000)

        val formattedDate = formatDatePretty(calendar.time)

        formattedDate shouldBeEqualTo "January 01, 2000"
    }

    @Test
    fun `should format date to input string`() {
        val calendar = Calendar.getInstance()

        calendar.set(MONTH, NOVEMBER)
        calendar.set(DAY_OF_MONTH, 20)
        calendar.set(YEAR, 1695)

        val formattedDate = formatDateForInput(calendar.time)

        formattedDate shouldBeEqualTo "11-20-1695"
    }

    @Test
    fun `should pad zeros of the day when formatting date to input string`() {
        val calendar = Calendar.getInstance()

        calendar.set(MONTH, DECEMBER)
        calendar.set(DAY_OF_MONTH, 1)
        calendar.set(YEAR, 2000)

        val formattedDate = formatDateForInput(calendar.time)

        formattedDate shouldBeEqualTo "12-01-2000"
    }

    @Test
    fun `should pad zeros of the month when formatting date to input string`() {
        val calendar = Calendar.getInstance()

        calendar.set(MONTH, JANUARY)
        calendar.set(DAY_OF_MONTH, 10)
        calendar.set(YEAR, 2000)

        val formattedDate = formatDateForInput(calendar.time)

        formattedDate shouldBeEqualTo "01-10-2000"
    }
}