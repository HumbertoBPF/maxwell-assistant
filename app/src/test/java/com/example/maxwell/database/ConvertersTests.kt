package com.example.maxwell.database

import com.example.maxwell.models.Currency
import com.example.maxwell.models.FinanceType
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.Test
import java.math.BigDecimal
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

class ConvertersTests {
    private val converters = Converters()

    @Test
    fun `should convert date to long`() {
        val calendar = Calendar.getInstance()

        calendar.set(MONTH, 11)
        calendar.set(DAY_OF_MONTH, 20)
        calendar.set(YEAR, 1965)

        val date = calendar.time

        converters.fromDateToLong(date) shouldBeEqualTo date.time
    }

    @Test
    fun `should convert long to date`() {
        val calendar = Calendar.getInstance()

        calendar.set(MONTH, 11)
        calendar.set(DAY_OF_MONTH, 20)
        calendar.set(YEAR, 1965)

        val date = calendar.time

        converters.fromLongToDate(date.time) shouldBeEqualTo date
    }

    @Test
    fun `should convert big decimal to string`() {
        val bigDecimal = BigDecimal.valueOf(33.5)
        converters.fromBigDecimalToString(bigDecimal) shouldBeEqualTo "33.5"
    }

    @Test
    fun `should convert string to big decimal`() {
        val bigDecimal = BigDecimal.valueOf(33.5)
        converters.fromStringToBigDecimal("33.5") shouldBeEqualTo bigDecimal
    }

    @Test
    fun `should convert BRL currency to string`() {
        val brl = Currency.BRL
        converters.fromCurrencyToString(brl) shouldBeEqualTo "BRL"
    }

    @Test
    fun `should convert EUR currency to string`() {
        val eur = Currency.EUR
        converters.fromCurrencyToString(eur) shouldBeEqualTo "EUR"
    }

    @Test
    fun `should convert BRL string to BRL currency`() {
        converters.fromStringToCurrency("BRL") shouldBeEqualTo Currency.BRL
    }

    @Test
    fun `should convert EUR string to EUR currency`() {
        converters.fromStringToCurrency("EUR") shouldBeEqualTo Currency.EUR
    }

    @Test
    fun `should convert string to null when it does not match any of the currencies`() {
        converters.fromStringToCurrency("INVALID").shouldBeNull()
    }

    @Test
    fun `should convert EXPENSE finance type to string`() {
        val expense = FinanceType.EXPENSE
        converters.fromFinanceTypeToString(expense) shouldBeEqualTo "Expense"
    }

    @Test
    fun `should convert INCOME finance type to string`() {
        val income = FinanceType.INCOME
        converters.fromFinanceTypeToString(income) shouldBeEqualTo "Income"
    }

    @Test
    fun `should convert Expense string to EXPENSE finance type`() {
        converters.fromStringToFinanceType("Expense") shouldBeEqualTo FinanceType.EXPENSE
    }

    @Test
    fun `should convert Income string to INCOME finance type`() {
        converters.fromStringToFinanceType("Income") shouldBeEqualTo FinanceType.INCOME
    }

    @Test
    fun `should convert string to null when it does not match with any FinanceType`() {
        converters.fromStringToFinanceType("INVALID").shouldBeNull()
    }

    @Test
    fun `should convert PENDING status to string`() {
        val pending = Status.PENDING
        converters.fromStatusToString(pending) shouldBeEqualTo "Pending"
    }

    @Test
    fun `should convert IN_PROGRESS status to string`() {
        val inProgress = Status.IN_PROGRESS
        converters.fromStatusToString(inProgress) shouldBeEqualTo "In Progress"
    }

    @Test
    fun `should convert DONE status to string`() {
        val done = Status.DONE
        converters.fromStatusToString(done) shouldBeEqualTo "Done"
    }

    @Test
    fun `should convert Pending string to PENDING Status`() {
        converters.fromStringToStatus("Pending") shouldBeEqualTo Status.PENDING
    }

    @Test
    fun `should convert In Progress string to IN_PROGRESS Status`() {
        converters.fromStringToStatus("In Progress") shouldBeEqualTo Status.IN_PROGRESS
    }

    @Test
    fun `should convert Done string to DONE Status`() {
        converters.fromStringToStatus("Done") shouldBeEqualTo Status.DONE
    }

    @Test
    fun `should convert string to null if it does not match any Status`() {
        converters.fromStringToStatus("Invalid").shouldBeNull()
    }

    @Test
    fun `should convert LOW priority to string`() {
        val low = Priority.LOW
        converters.fromPriorityToString(low) shouldBeEqualTo "Low"
    }

    @Test
    fun `should convert MEDIUM priority to string`() {
        val medium = Priority.MEDIUM
        converters.fromPriorityToString(medium) shouldBeEqualTo "Medium"
    }

    @Test
    fun `should convert HIGH priority to string`() {
        val high = Priority.HIGH
        converters.fromPriorityToString(high) shouldBeEqualTo "High"
    }

    @Test
    fun `should convert Low string to LOW priority`() {
        converters.fromStringToPriority("Low") shouldBeEqualTo Priority.LOW
    }

    @Test
    fun `should convert Medium string to MEDIUM priority`() {
        converters.fromStringToPriority("Medium") shouldBeEqualTo Priority.MEDIUM
    }

    @Test
    fun `should convert High string to HIGH priority`() {
        converters.fromStringToPriority("High") shouldBeEqualTo Priority.HIGH
    }

    @Test
    fun `should convert string to null if it does not match any priority`() {
        converters.fromStringToPriority("Invalid").shouldBeNull()
    }
}