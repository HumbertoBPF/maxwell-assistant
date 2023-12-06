package com.example.maxwell.models

import com.example.maxwell.R

enum class FinanceType(val text: String, val stringResource: Int, val symbol: String, val color: Int) {
    INCOME("Income", R.string.income_radio_button, "+", R.color.income_color),
    EXPENSE("Expense", R.string.expense_radio_button, "-", R.color.expense_color)
}