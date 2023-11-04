package com.example.maxwell.models

import com.example.maxwell.R

enum class Priority(
    val text: String,
    val stringResource: Int,
    val iconResource: Int?
) {
    LOW("Low", R.string.priority_enum_low_importance, null),
    MEDIUM("Medium", R.string.priority_enum_medium_importance, R.drawable.ic_medium_priority),
    HIGH("High", R.string.priority_enum_high_importance, R.drawable.ic_high_priority)
}