package com.example.maxwell.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity
data class Study (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String?,
    val duration: BigDecimal?,
    val subjectId: Long,
    val links: String?,
    val status: Status?,
    val startingDate: Date?,
)