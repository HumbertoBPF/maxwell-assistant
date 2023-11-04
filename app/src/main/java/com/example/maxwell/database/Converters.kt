package com.example.maxwell.database

import androidx.room.TypeConverter
import com.example.maxwell.models.Currency
import com.example.maxwell.models.FinanceType
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import java.math.BigDecimal
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDateToString(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun fromStringToDate(long: Long): Date {
        return Date(long)
    }

    @TypeConverter
    fun fromBigDecimalToString(bigDecimal: BigDecimal): String {
        return bigDecimal.toString()
    }

    @TypeConverter
    fun fromStringToBigDecimal(string: String): BigDecimal {
        return BigDecimal(string)
    }

    @TypeConverter
    fun fromCurrencyToString(currency: Currency): String {
        return currency.text
    }

    @TypeConverter
    fun fromStringToCurrency(string: String): Currency? {
        val currencies = listOf(Currency.BRL, Currency.EUR)

        currencies.forEach { currency ->
            if (currency.text == string) {
                return currency
            }
        }

        return null
    }

    @TypeConverter
    fun fromFinanceTypeToString(financeType: FinanceType): String {
        return financeType.text
    }

    @TypeConverter
    fun fromStringToFinanceType(string: String): FinanceType? {
        val types = listOf(FinanceType.INCOME, FinanceType.EXPENSE)

        types.forEach { type ->
            if (type.text == string) {
                return type
            }
        }

        return null
    }

    @TypeConverter
    fun fromStatusToString(status: Status): String {
        return status.text
    }

    @TypeConverter
    fun fromStringToStatus(string: String): Status? {
        val statuses = listOf(Status.PENDING, Status.IN_PROGRESS, Status.DONE)

        statuses.forEach { status ->
            if (status.text == string) {
                return status
            }
        }

        return null
    }

    @TypeConverter
    fun fromPriorityToString(priority: Priority): String {
        return priority.text
    }

    @TypeConverter
    fun fromStringToPriority(string: String): Priority? {
        val priorities = listOf(Priority.LOW, Priority.MEDIUM, Priority.HIGH)

        priorities.forEach { priority ->
            if (priority.text == string) {
                return priority
            }
        }

        return null
    }
}