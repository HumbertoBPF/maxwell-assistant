package com.example.maxwell.utils.activities.base

import com.example.maxwell.utils.UITests

open class TaskTests: UITests() {
    protected val taskDao = db.taskDao()
}