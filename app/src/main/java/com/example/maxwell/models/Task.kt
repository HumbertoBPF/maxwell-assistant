package com.example.maxwell.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity
data class Task (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String?,
    val dueDate: Date?,
    val priority: Priority?,
    val status: Status?,
    val duration: BigDecimal?
)