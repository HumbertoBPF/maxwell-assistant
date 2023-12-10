package com.example.maxwell.tests.tasks

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.utils.activities.base.TaskTests
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getTasksForTests
import com.example.maxwell.utils.hasLength
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Test

class TasksActivityTests: TaskTests() {
    private val tasks = getTasksForTests()

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            taskDao.insert(*tasks)
        }
    }

    @Test
    fun shouldDisplayTasks() {
        navigateToTheTasksActivity()

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                allOf(
                    hasLength(3),
                    taskAtPosition(0, tasks[0]),
                    taskAtPosition(1, tasks[1]),
                    taskAtPosition(2, tasks[2])
                )
            ))

        onView(withId(R.id.add_fab)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_filter)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDisplayFilterTasksDialog() {
        navigateToTheTasksActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.title_label))
            ))

        onView(withId(R.id.due_to_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.due_to_label))
            ))

        onView(withId(R.id.priority_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.priority_label))
            ))

        onView(withId(R.id.status_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.status_label))
            ))
    }

    @Test
    fun shouldFilterTasksByTitle() {
        val randomTask = getRandomElement(tasks)

        navigateToTheTasksActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .perform(typeText(randomTask.title), closeSoftKeyboard())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                allOf(
                    hasLength(1),
                    taskAtPosition(0, randomTask)
                )
            ))
    }

    @Test
    fun shouldFilterTasksByDueDate() {
        navigateToTheTasksActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        fillDatePickerInput(R.id.due_to_text_input_edit_text, "11-30-2023")

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                allOf(
                    hasLength(2),
                    taskAtPosition(0, tasks[0]),
                    taskAtPosition(1, tasks[1])
                )
            ))
    }

    @Test
    fun shouldFilterTasksByPriority() {
        val randomTask = getRandomElement(tasks)

        navigateToTheTasksActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.priority_text_input_auto_complete)).perform(click())

        onData(`is`(randomTask.priority?.text))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                allOf(
                    hasLength(1),
                    taskAtPosition(0, randomTask)
                )
            ))
    }

    @Test
    fun shouldFilterTasksByStatus() {
        val randomTask = getRandomElement(tasks)

        navigateToTheTasksActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.status_text_input_auto_complete)).perform(click())

        onData(`is`(randomTask.status?.text))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                allOf(
                    hasLength(1),
                    taskAtPosition(0, randomTask)
                )
            ))
    }

    @Test
    fun shouldFilterTasksWithMultipleFilters() {
        val randomTask = getRandomElement(tasks)

        navigateToTheTasksActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .perform(typeText(randomTask.title), closeSoftKeyboard())

        val dueDate = randomTask.dueDate ?: throw NullPointerException("Due date should not be null")
        fillDatePickerInput(R.id.due_to_text_input_edit_text, formatDateForInput(dueDate))

        onView(withId(R.id.priority_text_input_auto_complete)).perform(click())

        onData(`is`(randomTask.priority?.text))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(withId(R.id.status_text_input_auto_complete)).perform(click())

        onData(`is`(randomTask.status?.text))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                allOf(
                    hasLength(1),
                    taskAtPosition(0, randomTask)
                )
            ))
    }

    private fun navigateToTheTasksActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(0, click())
            )
    }
}