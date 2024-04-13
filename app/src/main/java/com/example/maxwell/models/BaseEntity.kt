package com.example.maxwell.models

import androidx.room.ColumnInfo
import java.util.Calendar

open class BaseEntity(
    @ColumnInfo(defaultValue = "0")
    var timestampModified: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo(defaultValue = "false")
    var deleted: Boolean = false
) {
    fun setToDeleted() {
        deleted = true
        timestampModified = Calendar.getInstance().timeInMillis
    }
}