package com.example.maxwell.utils

import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import java.math.BigDecimal
import java.util.Calendar

fun getTasksForTests(): Array<Task> = arrayOf(
    Task(
        id = 1,
        title = "First task",
        description = "Description for the first task",
        dueDate = getCalendar(2023, Calendar.NOVEMBER, 30).time,
        priority = Priority.LOW,
        status = Status.PENDING,
        duration = BigDecimal(1.0)
    ),
    Task(
        id = 2,
        title = "Second task",
        description = "Description for the second task",
        dueDate = getCalendar(2023, Calendar.NOVEMBER, 30).time,
        priority = Priority.MEDIUM,
        status = Status.IN_PROGRESS,
        duration = BigDecimal(1.5)
    ),
    Task(
        id = 3,
        title = "Third task",
        description = "Description for the third task",
        dueDate = getCalendar(2023, Calendar.NOVEMBER, 24).time,
        priority = Priority.HIGH,
        status = Status.DONE,
        duration = BigDecimal(2.5)
    )
)