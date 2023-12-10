package com.example.maxwell.tests.tasks

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import com.example.maxwell.utils.activities.forms.TaskFormActivityTests
import com.example.maxwell.utils.getCalendar
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.hasError
import com.example.maxwell.utils.hasLength
import com.example.maxwell.utils.parseDate
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.util.Calendar.DECEMBER

class AddTaskFormActivityTests: TaskFormActivityTests() {
    private val title = "Task title"
    private val description = "Task description"
    private val duration = "2.0"
    private val dueDate = "12-02-2023"
    private val priority = getRandomElement(arrayOf(Priority.HIGH, Priority.MEDIUM, Priority.LOW))
    private val status = getRandomElement(arrayOf(Status.PENDING, Status.IN_PROGRESS, Status.DONE))

    @Test
    fun shouldRenderTheTaskFormEmptyWhenAddingATask() {
        navigateToTheAddTaskScreen()

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.title_text_input_label))
            ))

        onView(withId(R.id.description_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.description_text_input_label))
            ))

        onView(withId(R.id.duration_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.duration_text_input_label))
            ))

        onView(withId(R.id.due_date_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.due_date_text_input_label))
            ))

        onView(withId(R.id.priority_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.priority_text_input_label))
            ))

        onView(withId(R.id.status_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.status_text_input_label))
            ))

        onView(withId(R.id.save_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.save_button_text))
            ))
    }

    @Test
    fun shouldAddNewTask() {
        navigateToTheAddTaskScreen()

        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = dueDate,
            priority = priority,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.tasks_recycler_view)).check(matches(
            allOf(
                hasLength(1),
                taskAtPosition(
                    0,
                    Task(
                        title = title,
                        description = description,
                        duration = BigDecimal(duration),
                        dueDate = parseDate(dueDate),
                        priority = priority,
                        status = status
                    )
                )
            )
        ))

        val tasks = runBlocking {
            taskDao.filterTasks(SimpleSQLiteQuery("SELECT * FROM Task;"))
        }

        assertEquals(tasks.size, 1)

        val task = tasks[0]

        assertEquals(title, task.title)
        assertEquals(description, task.description)
        assertEquals(duration, task.duration.toString())
        assertEquals(getCalendar(2023, DECEMBER, 2).time, task.dueDate)
        assertEquals(priority, task.priority)
        assertEquals(status, task.status)
    }

    @Test
    fun shouldRequireTitle() {
        navigateToTheAddTaskScreen()

        fillTaskForm(
            title = "",
            description = description,
            duration = duration,
            dueDate = dueDate,
            priority = priority,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.title_text_input))
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireDuration() {
        navigateToTheAddTaskScreen()

        fillTaskForm(
            title = title,
            description = description,
            duration = "",
            dueDate = dueDate,
            priority = priority,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.duration_text_input))
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireDueDate() {
        navigateToTheAddTaskScreen()

        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = "",
            priority = priority,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.due_date_text_input))
            .check(matches(hasError(R.string.data_format_instruction)))
    }

    @Test
    fun shouldRequirePriority() {
        navigateToTheAddTaskScreen()

        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = dueDate,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.priority_text_input))
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireStatus() {
        navigateToTheAddTaskScreen()

        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = dueDate,
            priority = priority
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.status_text_input))
            .check(matches(hasError(R.string.required_field_error)))
    }

    private fun navigateToTheAddTaskScreen() {
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(0, click())
            )

        onView(withId(R.id.add_fab)).perform(click())
    }
}