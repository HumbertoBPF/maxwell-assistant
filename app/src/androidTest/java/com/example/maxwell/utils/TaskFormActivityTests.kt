package com.example.maxwell.utils

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers
import com.example.maxwell.R
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import org.hamcrest.CoreMatchers.`is`

open class TaskFormActivityTests: UITests() {
    protected val taskDao = db.taskDao()

    protected fun fillTaskForm(
        title: String,
        description: String,
        duration: String,
        dueDate: String
    ) {
        Espresso.onView(ViewMatchers.withId(R.id.title_text_input_edit_text))
            .perform(
                clearText(),
                typeText(title),
                closeSoftKeyboard()
            )

        Espresso.onView(ViewMatchers.withId(R.id.description_text_input_edit_text))
            .perform(
                clearText(),
                typeText(description),
                closeSoftKeyboard()
            )

        Espresso.onView(ViewMatchers.withId(R.id.duration_text_input_edit_text))
            .perform(
                clearText(),
                typeText(duration),
                closeSoftKeyboard()
            )

        fillDatePickerInput(R.id.due_date_text_input_edit_text, dueDate)
    }

    protected fun fillTaskForm(
        title: String,
        description: String,
        duration: String,
        dueDate: String,
        priority: Priority,
        status: Status
    ) {
        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = dueDate,
            priority = priority
        )

        Espresso.onView(ViewMatchers.withId(R.id.status_text_input_auto_complete))
            .perform(click())

        Espresso.onData(`is`(status.text))
            .inRoot(isPlatformPopup())
            .perform(click())
    }

    protected fun fillTaskForm(
        title: String,
        description: String,
        duration: String,
        dueDate: String,
        status: Status
    ) {
        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = dueDate
        )

        Espresso.onView(ViewMatchers.withId(R.id.status_text_input_auto_complete))
            .perform(click())

        Espresso.onData(`is`(status.text))
            .inRoot(isPlatformPopup())
            .perform(click())
    }

    protected fun fillTaskForm(
        title: String,
        description: String,
        duration: String,
        dueDate: String,
        priority: Priority
    ) {
        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = dueDate
        )

        Espresso.onView(ViewMatchers.withId(R.id.priority_text_input_auto_complete))
            .perform(click())

        Espresso.onData(`is`(priority.text))
            .inRoot(isPlatformPopup())
            .perform(click())
    }
}