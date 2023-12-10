package com.example.maxwell.tests.tasks

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
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getTasksForTests
import com.example.maxwell.utils.hasError
import com.example.maxwell.utils.parseDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class EditTaskFormActivityTests: TaskFormActivityTests() {
    private val tasks = getTasksForTests()
    private val selectedTask = getRandomElement(tasks)

    private val title = "Task title"
    private val description = "Task description"
    private val duration = "2.0"
    private val dueDate = "12-02-2023"
    private val priority = getRandomElement(arrayOf(Priority.HIGH, Priority.MEDIUM, Priority.LOW))
    private val status = getRandomElement(arrayOf(Status.PENDING, Status.IN_PROGRESS, Status.DONE))

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            taskDao.insert(*tasks)
        }
    }

    @Test
    fun shouldDisplayTaskFormFilledWithDataOfTheSelectedTask() {
        navigateToEditTaskFormActivity()

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.title_text_input_label),
                    withText(selectedTask.title)
                )
            ))

        onView(withId(R.id.description_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.description_text_input_label),
                    withText(selectedTask.description)
                )
            ))

        onView(withId(R.id.duration_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.duration_text_input_label),
                    withText("${selectedTask.duration}")
                )
            ))

        val expectedDueDate = formatDateForInput(
            selectedTask.dueDate ?: throw NullPointerException("Due date should not be null")
        )

        onView(withId(R.id.due_date_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.due_date_text_input_label),
                    withText(expectedDueDate)
                )
            ))

        onView(withId(R.id.priority_text_input_auto_complete))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.priority_text_input_label),
                    withText(selectedTask.priority?.text)
                )
            ))

        onView(withId(R.id.status_text_input_auto_complete))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.status_text_input_label),
                    withText(selectedTask.status?.text)
                )
            ))

        onView(withId(R.id.save_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.save_button_text))
            ))
    }

    @Test
    fun shouldUpdateTheSelectedTask() {
        navigateToEditTaskFormActivity()

        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = dueDate,
            priority = priority,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        assertTaskDetails(
            Task(
                title = title,
                description = description,
                duration = BigDecimal(duration),
                dueDate = parseDate(dueDate),
                priority = priority,
                status = status
            )
        )

        val updatedTask = runBlocking {
            taskDao.getTaskById(selectedTask.id).first()
        }

        assertEquals(title, updatedTask?.title)
        assertEquals(description, updatedTask?.description)
        assertEquals(BigDecimal(duration), updatedTask?.duration)
        assertEquals(parseDate(dueDate), updatedTask?.dueDate)
        assertEquals(priority, updatedTask?.priority)
        assertEquals(status, updatedTask?.status)
    }

    @Test
    fun shouldRequireTitle() {
        navigateToEditTaskFormActivity()

        fillTaskForm(
            title = "",
            description = description,
            duration = duration,
            dueDate = dueDate
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.title_text_input))
            .check(matches(
                hasError(R.string.required_field_error)
            ))
    }

    @Test
    fun shouldRequireDuration() {
        navigateToEditTaskFormActivity()

        fillTaskForm(
            title = title,
            description = description,
            duration = "",
            dueDate = dueDate
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.duration_text_input))
            .check(matches(
                hasError(R.string.required_field_error)
            ))
    }

    @Test
    fun shouldRequireDueDate() {
        navigateToEditTaskFormActivity()

        fillTaskForm(
            title = title,
            description = description,
            duration = duration,
            dueDate = ""
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.due_date_text_input))
            .check(matches(
                hasError(R.string.data_format_instruction)
            ))
    }

    private fun navigateToEditTaskFormActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(0, click()))

        onView(withText(selectedTask.title)).perform(click())

        onView(withId(R.id.edit_item)).perform(click())
    }
}