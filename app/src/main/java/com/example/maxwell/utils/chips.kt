package com.example.maxwell.utils

import android.content.Context
import com.google.android.material.chip.Chip

fun createChipView(context: Context, text: String): Chip {
    val chip = Chip(context)

    chip.text = text
    chip.isChipIconVisible = false
    chip.isCloseIconVisible = true
    chip.isClickable = true
    chip.isCheckable = false

    return chip
}