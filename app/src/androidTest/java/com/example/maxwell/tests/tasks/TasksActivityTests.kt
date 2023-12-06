package com.example.maxwell.tests.tasks

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.models.Task
import com.example.maxwell.utils.activities.base.TaskTests
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.getTasksForTests
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
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
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(0, click())
            )

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                taskAtPosition(0, tasks[0])
            ))

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                taskAtPosition(1, tasks[1])
            ))

        onView(withId(R.id.tasks_recycler_view))
            .check(matches(
                taskAtPosition(2, tasks[2])
            ))

        onView(withId(R.id.add_fab)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_filter)).check(matches(isDisplayed()))
    }

    private fun taskAtPosition(position: Int, task: Task): Matcher<in View> {
        val titleMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.title_text_view),
                withText(task.title)
            )
        )

        val durationMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.duration_text_view),
                withText("${task.duration} h")
            )
        )

        val priorityText = context.getString(task.priority?.stringResource ?: -1)

        val priorityMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.priority_text_view),
                withText(priorityText)
            )
        )

        val statusText = context.getString(task.status?.stringResource ?: -1)

        val statusMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.status_text_view),
                withText(statusText)
            )
        )

        val taskMatcher = allOf(
            titleMatcher,
            durationMatcher,
            priorityMatcher,
            statusMatcher
        )

        return atPosition(position, taskMatcher)
    }
}