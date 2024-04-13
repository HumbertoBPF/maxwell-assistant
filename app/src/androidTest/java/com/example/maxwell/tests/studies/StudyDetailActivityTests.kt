package com.example.maxwell.tests.studies

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.utils.activities.base.StudyTests
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getStudiesForTests
import com.example.maxwell.utils.getStudySubjectsForTests
import com.example.maxwell.utils.hasLength
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class StudyDetailActivityTests: StudyTests() {
    private val studies = getStudiesForTests()
    private val studySubjects = getStudySubjectsForTests()

    private val selectedStudy = getRandomElement(studies)

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            studySubjectDao.insert(*studySubjects)
            studyDao.insert(*studies)
        }
    }

    @Test
    fun shouldDisplayDetailsOfTheSelectedStudy() {
        navigateToTheStudyDetailActivity()

        onView(withId(R.id.edit_item)).check(matches(isDisplayed()))
        onView(withId(R.id.delete_item)).check(matches(isDisplayed()))

        assertStudyDetails(selectedStudy)
    }

    @Test
    fun shouldDeleteSelectedStudy() {
        navigateToTheStudyDetailActivity()

        onView(withId(R.id.delete_item)).perform(click())

        onView(withText(R.string.confirm_deletion_dialog_positive_button)).perform(click())

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                allOf(
                    hasLength(2),
                    not(studyAtPosition(0, selectedStudy)),
                    not(studyAtPosition(1, selectedStudy))
                )
            ))

        val study = runBlocking {
            studyDao.getById(selectedStudy.id).first()
        }

        assertNull(study)
    }

    private fun navigateToTheStudyDetailActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(1, click()))

        onView(withText(selectedStudy.title)).perform(click())
    }
}