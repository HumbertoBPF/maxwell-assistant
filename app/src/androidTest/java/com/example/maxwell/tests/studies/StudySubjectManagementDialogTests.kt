package com.example.maxwell.tests.studies

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.utils.activities.base.StudyTests
import com.example.maxwell.utils.closeChip
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getStudySubjectsForTests
import com.example.maxwell.utils.hasError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class StudySubjectManagementDialogTests: StudyTests() {
    private val studySubjects = getStudySubjectsForTests()

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            studySubjectDao.insert(*studySubjects)
        }
    }

    @Test
    fun shouldDisplayStudySubjectsOnTheDialog() {
        navigateToTheStudyForm()

        onView(withId(R.id.manage_study_subject_text_view)).perform(click())

        onView(withText(studySubjects[2].name)).perform(scrollTo())

        onView(withId(R.id.study_subjects_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(studySubjects[2].name))
                )
            ))

        onView(withText(studySubjects[1].name)).perform(scrollTo())

        onView(withId(R.id.study_subjects_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(studySubjects[1].name))
                )
            ))

        onView(withText(studySubjects[0].name)).perform(scrollTo())

        onView(withId(R.id.study_subjects_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(studySubjects[0].name))
                )
            ))
    }

    @Test
    fun shouldAddStudySubject() {
        val newStudySubjectName = "New Study Subject Name"
        val startTimestamp = Calendar.getInstance().timeInMillis

        navigateToTheStudyForm()

        onView(withId(R.id.manage_study_subject_text_view)).perform(click())

        onView(withId(R.id.name_text_input_edit_text))
            .perform(typeText(newStudySubjectName), closeSoftKeyboard())

        onView(withText(R.string.study_subject_dialog_positive_button)).perform(click())

        val endTimestamp = Calendar.getInstance().timeInMillis

        // Checking if the created study subject was added to the list displayed on the dialog
        onView(withText(newStudySubjectName)).perform(scrollTo())

        onView(withId(R.id.study_subjects_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(newStudySubjectName))
                )
            ))
        // Checking if a new study subject was created in the database
        val newStudySubject = runBlocking {
            studySubjectDao.getByName(newStudySubjectName)
        }

        assertEquals(newStudySubjectName, newStudySubject?.name)
        assertTrue(newStudySubject?.timestampModified in startTimestamp..endTimestamp)
        assertEquals(false, newStudySubject?.deleted)
    }

    @Test
    fun shouldNotAddTheStudySubjectIfThereIsASubjectWithTheSameName() {
        val randomStudySubject = getRandomElement(studySubjects)

        navigateToTheStudyForm()

        onView(withId(R.id.manage_study_subject_text_view)).perform(click())

        onView(withId(R.id.name_text_input_edit_text))
            .perform(typeText(randomStudySubject.name), closeSoftKeyboard())

        onView(withText(R.string.study_subject_dialog_positive_button)).perform(click())

        onView(withId(R.id.name_text_input_layout))
            .check(matches(hasError(R.string.error_study_subject_name_unavailable)))
    }

    @Test
    fun shouldDeleteStudySubject() {
        val randomStudySubject = getRandomElement(studySubjects)

        navigateToTheStudyForm()

        onView(withId(R.id.manage_study_subject_text_view)).perform(click())

        onView(withText(randomStudySubject.name))
            .perform(scrollTo(), closeChip())

        onView(withId(R.id.study_subjects_chip_group))
            .check(matches(not(
                hasDescendant(
                    allOf(isDisplayed(), withText(randomStudySubject.name))
                )
            )))

        val studySubject = runBlocking {
            studySubjectDao.getById(randomStudySubject.id).first()
        }

        assertNull(studySubject)
    }

    private fun navigateToTheStudyForm() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(1, click()))

        onView(withId(R.id.add_fab)).perform(click())
    }
}