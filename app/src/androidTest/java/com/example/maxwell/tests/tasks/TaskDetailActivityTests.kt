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
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getTasksForTests
import com.example.maxwell.utils.hasLength
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
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

        assertTaskDetails(selectedTask)
    }

    @Test
    fun shouldDeleteTask() {
        navigateToTheTaskDetailActivity()

        onView(withId(R.id.delete_item)).perform(click())

        onView(withText(R.string.confirm_deletion_dialog_positive_button)).perform(click())

        onView(withId(R.id.tasks_recycler_view)).check(matches(hasLength(2)))

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(not(
                taskAtPosition(0, selectedTask)
            )))

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(not(
                taskAtPosition(1, selectedTask)
            )))

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
}