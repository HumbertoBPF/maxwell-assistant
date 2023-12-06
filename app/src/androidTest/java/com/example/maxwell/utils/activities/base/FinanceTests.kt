package com.example.maxwell.utils.activities.base

import com.example.maxwell.utils.UITests

open class FinanceTests: UITests() {
    protected val financeCategoryDao = db.financeCategoryDao()
    protected val financeDao = db.financeDao()
}