package com.example.maxwell.utils

import android.content.Context
import android.content.DialogInterface
import com.example.maxwell.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showConfirmDeletionDialog(
    context: Context,
    positiveButtonListener: DialogInterface.OnClickListener
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(R.string.confirm_deletion_dialog_title)
        .setMessage(R.string.confirm_deletion_dialog_message)
        .setNegativeButton(R.string.confirm_deletion_dialog_negative_button) { dialog, _ ->
            dialog.dismiss()
        }
        .setPositiveButton(R.string.confirm_deletion_dialog_positive_button, positiveButtonListener)
        .show()
}