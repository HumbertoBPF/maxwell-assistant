package com.example.maxwell.models

import androidx.room.Embedded
import androidx.room.Relation

data class FinanceCategoryWithFinances (
    @Embedded val financeType: FinanceType,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val finances: List<Finance>
)