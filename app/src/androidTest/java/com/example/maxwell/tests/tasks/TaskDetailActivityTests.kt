package com.example.maxwell.tests.tasks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.utils.activities.base.TaskTests
import com.example.maxwell.utils.formatDatePretty
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getTasksForTests
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TaskDetailActivityTests: TaskTests() {
    private val tasks = getTasksForTests()
    private val selectedTask = getRandomElement(tasks)

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            taskDao.insert(*tasks)
        }
    }

    @Test
    fun shouldDisplayTheDetailsOfTheSelectedTask() {
        navigateToTheTaskDetailActivity()

        onView(withId(R.id.edit_item)).check(matches(isDisplayed()))
        onView(withId(R.id.delete_item)).check(matches(isDisplayed()))

        assertTaskDetails()
    }

    @Test
    fun shouldDeleteTask() {
        navigateToTheTaskDetailActivity()

        onView(withId(R.id.delete_item)).perform(click())

        onView(withText(R.string.confirm_deletion_dialog_positive_button)).perform(click())

        val task = runBlocking {
            taskDao.getTaskById(selectedTask.id).first()
        }

        assertNull(task)
    }

    private fun navigateToTheTaskDetailActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(0, click())
            )

        onView(withText(selectedTask.title)).perform(click())
    }

    private fun assertTaskDetails() {
        onView(withId(R.id.title_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText(selectedTask.title))
                )
            )

        onView(withId(R.id.description_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText(selectedTask.description))
                )
            )

        onView(withId(R.id.duration_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText("${selectedTask.duration} h"))
                )
            )

        onView(withId(R.id.due_to_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(
                            formatDatePretty(
                                selectedTask.dueDate
                                    ?: throw NullPointerException("Date cannot be null in this test")
                            )
                        )
                    )
                )
            )

        onView(withId(R.id.priority_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(selectedTask.priority?.stringResource ?: -1))
                )
            )


        onView(withId(R.id.status_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(selectedTask.status?.stringResource ?: -1))
                )
            )
    }
}