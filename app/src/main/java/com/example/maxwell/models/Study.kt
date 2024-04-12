package com.example.maxwell.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

@Entity
data class Study (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String?,
    val duration: BigDecimal?,
    @SerializedName("subject_id")
    val subjectId: Long,
    val links: String?,
    val status: Status?,
    @SerializedName("starting_date")
    val startingDate: Date?,
)