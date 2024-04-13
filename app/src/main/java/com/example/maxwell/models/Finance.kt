package com.example.maxwell.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

@Entity
open class Finance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    @SerializedName("category_id") val categoryId: Long,
    val value: BigDecimal?,
    val currency: Currency?,
    val type: FinanceType?,
    val date: Date?
): BaseEntity() {
    fun formatValue(): String {
        if ((currency != null) && (type != null)) {
            val currencyFormatter = currency.formatter
            val formattedValue = currencyFormatter.format(value)

            return "${type.symbol}$formattedValue"
        }

        return ""
    }
}