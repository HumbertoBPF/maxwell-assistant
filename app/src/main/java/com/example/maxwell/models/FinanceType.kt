package com.example.maxwell.models

import com.example.maxwell.R

enum class FinanceType(val text: String, val symbol: String, val color: Int) {
    INCOME("Income", "+", R.color.income_color),
    EXPENSE("Expense", "-", R.color.expense_color)
}