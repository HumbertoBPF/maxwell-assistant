package com.example.maxwell.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.Date

@Entity
@Parcelize
data class Task (
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val description: String?,
    @SerializedName("due_date")
    val dueDate: Date?,
    val priority: Priority?,
    val status: Status?,
    val duration: BigDecimal?
) : Parcelable