package com.example.maxwell.activities

import androidx.appcompat.app.AppCompatActivity
import com.example.maxwell.R
import com.google.android.material.textfield.TextInputLayout

abstract class FormActivity : AppCompatActivity() {
    protected fun markFieldAsRequired(field: TextInputLayout) {
        field.isErrorEnabled = true
        field.error = getString(R.string.required_field_error)
    }

    protected fun clearErrors(field: TextInputLayout) {
        field.isErrorEnabled = false
        field.error = ""
    }
}