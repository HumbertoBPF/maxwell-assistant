package com.example.maxwell.utils

import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.Test

class ValidationsTests {
    @Test
    fun `should return true if the string is a date with format MM-DD-YYYY`() {
        val string = "11-20-1965"
        string.hasValidDateFormat().shouldBeTrue()
    }

    @Test
    fun `should validate if the string has the format of a date`() {
        val string = "random string"
        string.hasValidDateFormat().shouldBeFalse()
    }

    @Test
    fun `should validate if the string date has the correct separators`() {
        val string = "11/20/1965"
        string.hasValidDateFormat().shouldBeFalse()
    }

    @Test
    fun `should validate the range of the day in the date string`() {
        val string = "11-31-1965"
        string.hasValidDateFormat().shouldBeFalse()
    }

    @Test
    fun `should validate the range of the month in the date string`() {
        val string = "13-20-1965"
        string.hasValidDateFormat().shouldBeFalse()
    }
}