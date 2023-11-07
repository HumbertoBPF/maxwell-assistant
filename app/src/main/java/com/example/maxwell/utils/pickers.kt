package com.example.maxwell.utils

import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun getDatePicker(title: String, listener: (date: Date) -> Unit): MaterialDatePicker<Long> {
    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

    datePicker.addOnPositiveButtonClickListener {
        val selection = datePicker.selection

        selection?.let {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            listener(calendar.time)
        }
    }

    return datePicker
}