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
import com.example.maxwell.utils.formatDatePretty
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getStudiesForTests
import com.example.maxwell.utils.getStudySubjectsForTests
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
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

        assertStudyDetails()
    }

    @Test
    fun shouldDeleteSelectedStudy() {
        navigateToTheStudyDetailActivity()

        onView(withId(R.id.delete_item)).perform(click())

        onView(withText(R.string.confirm_deletion_dialog_positive_button)).perform(click())

        val study = runBlocking {
            studyDao.getStudyById(selectedStudy.id).first()
        }

        assertNull(study)
    }

    private fun navigateToTheStudyDetailActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(1, click()))

        onView(withText(selectedStudy.title)).perform(click())
    }

    private fun assertStudyDetails() {
        onView(withId(R.id.title_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText(selectedStudy.title))
                )
            )

        onView(withId(R.id.description_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText(selectedStudy.description))
                )
            )

        onView(withId(R.id.duration_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText("${selectedStudy.duration} h"))
                )
            )

        val expectedStudySubject = runBlocking {
            studySubjectDao.getStudySubjectById(selectedStudy.subjectId).first()
        }

        onView(withId(R.id.subject_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText(expectedStudySubject?.name))
                )
            )

        onView(withId(R.id.links_text_view))
            .check(
                matches(
                    allOf(isDisplayed(), withText(selectedStudy.links))
                )
            )

        onView(withId(R.id.status_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(selectedStudy.status?.stringResource ?: -1)
                    )
                )
            )

        onView(withId(R.id.starting_date_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(
                            formatDatePretty(
                                selectedStudy.startingDate
                                    ?: throw NullPointerException("Starting date should not be null")
                            )
                        )
                    )
                )
            )
    }
}