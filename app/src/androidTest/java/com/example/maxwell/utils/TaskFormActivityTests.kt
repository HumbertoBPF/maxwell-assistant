package com.example.maxwell.utils

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.example.maxwell.R
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import org.hamcrest.CoreMatchers

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
                ViewActions.clearText(),
                ViewActions.typeText(title),
                ViewActions.closeSoftKeyboard()
            )

        Espresso.onView(ViewMatchers.withId(R.id.description_text_input_edit_text))
            .perform(
                ViewActions.clearText(),
                ViewActions.typeText(description),
                ViewActions.closeSoftKeyboard()
            )

        Espresso.onView(ViewMatchers.withId(R.id.duration_text_input_edit_text))
            .perform(
                ViewActions.clearText(),
                ViewActions.typeText(duration),
                ViewActions.closeSoftKeyboard()
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
            .perform(ViewActions.click())

        Espresso.onData(CoreMatchers.`is`(status.text))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
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
            .perform(ViewActions.click())

        Espresso.onData(CoreMatchers.`is`(status.text))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
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
            .perform(ViewActions.click())

        Espresso.onData(CoreMatchers.`is`(priority.text))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
    }
}