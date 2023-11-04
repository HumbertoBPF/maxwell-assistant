package com.example.maxwell.models

import com.example.maxwell.R

enum class Status(
    val text: String,
    val stringResource: Int,
    val iconResource: Int?
) {
    PENDING("Pending", R.string.pending_enum_pending, R.drawable.ic_pending),
    IN_PROGRESS("In Progress", R.string.status_enum_in_progress, R.drawable.ic_in_progress),
    DONE("Done", R.string.status_enum_done, R.drawable.ic_done)
}