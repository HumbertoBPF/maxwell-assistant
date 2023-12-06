package com.example.maxwell.tests.studies

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
import com.example.maxwell.models.Study
import com.example.maxwell.utils.activities.base.StudyTests
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.getStudiesForTests
import com.example.maxwell.utils.getStudySubjectsForTests
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test

class StudiesActivityTests: StudyTests() {
    private val studySubjects = getStudySubjectsForTests()
    private val studies = getStudiesForTests()

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            studySubjectDao.insert(*studySubjects)
            studyDao.insert(*studies)
        }
    }

    @Test
    fun shouldDisplayStudies() {
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(1, click())
            )

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(0, studies[1])
            ))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(1, studies[2])
            ))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(2, studies[0])
            ))

        onView(withId(R.id.add_fab)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_filter)).check(matches(isDisplayed()))
    }

    private fun studyAtPosition(position: Int, study: Study): Matcher<in View> {
        val titleMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.title_text_view),
                withText(study.title)
            )
        )

        val durationMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.duration_text_view),
                withText("${study.duration} h")
            )
        )

        val statusText = context.getString(study.status?.stringResource ?: -1)

        val statusMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.status_text_view),
                withText(statusText)
            )
        )

        val studyMatcher = allOf(
            titleMatcher,
            durationMatcher,
            statusMatcher
        )

        return atPosition(position, studyMatcher)
    }
}