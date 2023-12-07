package com.example.maxwell.utils.activities.base

open class FinanceTests: UITests() {
    protected val financeCategoryDao = db.financeCategoryDao()
    protected val financeDao = db.financeDao()
}