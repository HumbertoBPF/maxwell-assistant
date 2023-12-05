package com.example.maxwell.utils

import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Study
import com.example.maxwell.models.StudySubject
import com.example.maxwell.models.Task
import java.math.BigDecimal
import java.util.Calendar
import java.util.Calendar.DECEMBER

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

fun getStudySubjectsForTests(): Array<StudySubject> = arrayOf(
    StudySubject(
        id = 1,
        name = "Study Subject 1"
    ),
    StudySubject(
        id = 2,
        name = "Study Subject 2"
    ),
    StudySubject(
        id = 3,
        name = "Study Subject 3"
    )
)

fun getStudiesForTests(): Array<Study> = arrayOf(
    Study(
        id = 1,
        title = "First study",
        description = "Description for the first study",
        duration = BigDecimal(1.0),
        subjectId = 1,
        links = "https://google.com",
        status = Status.IN_PROGRESS,
        startingDate = getCalendar(year = 2023, month = DECEMBER, day = 4).time
    ),
    Study(
        id = 2,
        title = "Second study",
        description = "Description for the second study",
        duration = BigDecimal(1.5),
        subjectId = 2,
        links = "https://wikipedia.com",
        status = Status.PENDING,
        startingDate = getCalendar(year = 2023, month = DECEMBER, day = 5).time
    ),
    Study(
        id = 3,
        title = "Third study",
        description = "Description for the third study",
        duration = BigDecimal(2.5),
        subjectId = 3,
        links = "https://google.com",
        status = Status.DONE,
        startingDate = getCalendar(year = 2023, month = DECEMBER, day = 5).time
    ),
)