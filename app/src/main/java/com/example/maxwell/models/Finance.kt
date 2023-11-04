package com.example.maxwell.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity
data class Finance(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val categoryId: Long,
    val value: BigDecimal?,
    val currency: Currency?,
    val type: FinanceType?,
    val date: Date?
)