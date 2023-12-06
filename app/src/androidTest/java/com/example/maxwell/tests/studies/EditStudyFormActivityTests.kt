package com.example.maxwell.tests.studies

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
import com.example.maxwell.models.Status
import com.example.maxwell.utils.activities.forms.StudyFormActivityTests
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getStudiesForTests
import com.example.maxwell.utils.getStudySubjectsForTests
import com.example.maxwell.utils.hasError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.text.SimpleDateFormat

class EditStudyFormActivityTests: StudyFormActivityTests() {
    private val studies = getStudiesForTests()
    private val studySubjects = getStudySubjectsForTests()

    private val selectedStudy = getRandomElement(studies)

    private val title = "Study title"
    private val description = "Study description"
    private val duration = "1.5"
    private val subject = getRandomElement(studySubjects)
    private val links = "https://google.com"
    private val status = getRandomElement(arrayOf(Status.PENDING, Status.IN_PROGRESS, Status.DONE))
    private val startingDate = "04-12-2023"

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            studyDao.insert(*studies)
            studySubjectDao.insert(*studySubjects)
        }
    }

    @Test
    fun shouldDisplayTheStudyFormFilledWithDataOfTheSelectedStudy() {
        navigateToEditStudyFormActivity()

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.title_text_input_label),
                    withText(selectedStudy.title)
                )
            ))

        onView(withId(R.id.description_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.description_text_input_label),
                    withText(selectedStudy.description)
                )
            ))

        onView(withId(R.id.duration_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.duration_text_input_label),
                    withText("${selectedStudy.duration}")
                )
            ))

        val subject = runBlocking {
            studySubjectDao.getStudySubjectById(selectedStudy.subjectId).first()
        }

        onView(withId(R.id.subject_text_input_auto_complete))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.subject_text_input_label),
                    withText(subject?.name)
                )
            ))

        onView(withId(R.id.links_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.links_text_input_label),
                    withText(selectedStudy.links)
                )
            ))

        onView(withId(R.id.status_text_input_auto_complete))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.status_text_input_label),
                    withText(selectedStudy.status?.text)
                )
            ))

        val expectedStartingDate = formatDateForInput(
            selectedStudy.startingDate ?: throw NullPointerException("Starting date should not be null")
        )

        onView(withId(R.id.starting_date_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.starting_date_text_input_label),
                    withText(expectedStartingDate)
                )
            ))

        onView(withId(R.id.save_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.save_button_text))
            ))
    }

    @Test
    fun shouldUpdateTheSelectedStudy() {
        navigateToEditStudyFormActivity()

        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate,
            status = status,
            subject = subject
        )

        onView(withId(R.id.save_button)).perform(click())

        val updatedStudy = runBlocking {
            studyDao.getStudyById(selectedStudy.id).first()
        }

        assertEquals(title, updatedStudy?.title)
        assertEquals(description, updatedStudy?.description)
        assertEquals(BigDecimal(duration), updatedStudy?.duration)
        assertEquals(links, updatedStudy?.links)

        val sdf = SimpleDateFormat("MM-dd-yyyy")
        assertEquals(sdf.parse(startingDate), updatedStudy?.startingDate)

        assertEquals(status, updatedStudy?.status)
        assertEquals(subject.id, updatedStudy?.subjectId)
    }

    @Test
    fun shouldRequireTitle() {
        navigateToEditStudyFormActivity()

        fillStudyForm(
            title = "",
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate,
            subject = subject,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.title_text_input_layout))
            .check(matches(
                hasError(R.string.required_field_error)
            ))
    }

    @Test
    fun shouldRequireDuration() {
        navigateToEditStudyFormActivity()

        fillStudyForm(
            title = title,
            description = description,
            duration = "",
            links = links,
            startingDate = startingDate,
            subject = subject,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.duration_text_input_layout))
            .check(matches(
                hasError(R.string.required_field_error)
            ))
    }

    @Test
    fun shouldRequireStartingDate() {
        navigateToEditStudyFormActivity()

        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = "",
            subject = subject,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.starting_date_text_input_layout))
            .check(matches(
                hasError(R.string.data_format_instruction)
            ))
    }

    private fun navigateToEditStudyFormActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(1, click()))

        onView(withText(selectedStudy.title)).perform(click())

        onView(withId(R.id.edit_item)).perform(click())
    }
}