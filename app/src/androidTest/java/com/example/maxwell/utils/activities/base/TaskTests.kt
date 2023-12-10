package com.example.maxwell.utils.activities.base

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.maxwell.R
import com.example.maxwell.models.Task
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.formatDatePretty
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

open class TaskTests: UITests() {
    protected val taskDao = db.taskDao()

    protected fun taskAtPosition(position: Int, task: Task): Matcher<in View> {
        val titleMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.title_text_view),
                ViewMatchers.withText(task.title)
            )
        )

        val durationMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.duration_text_view),
                ViewMatchers.withText("${task.duration} h")
            )
        )

        val priorityText = context.getString(task.priority?.stringResource ?: -1)

        val priorityMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.priority_text_view),
                ViewMatchers.withText(priorityText)
            )
        )

        val statusText = context.getString(task.status?.stringResource ?: -1)

        val statusMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.status_text_view),
                ViewMatchers.withText(statusText)
            )
        )

        val taskMatcher = CoreMatchers.allOf(
            titleMatcher,
            durationMatcher,
            priorityMatcher,
            statusMatcher
        )

        return atPosition(position, taskMatcher)
    }

    protected fun assertTaskDetails(task: Task) {
        Espresso.onView(ViewMatchers.withId(R.id.title_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(task.title)
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.description_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(task.description)
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.duration_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText("${task.duration} h")
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.due_to_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(
                            formatDatePretty(
                                task.dueDate
                                    ?: throw NullPointerException("Date cannot be null in this test")
                            )
                        )
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.priority_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(task.priority?.stringResource ?: -1)
                    )
                )
            )


        Espresso.onView(ViewMatchers.withId(R.id.status_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(task.status?.stringResource ?: -1)
                    )
                )
            )
    }
}