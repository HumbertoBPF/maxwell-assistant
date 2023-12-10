package com.example.maxwell.tests.studies

import androidx.sqlite.db.SimpleSQLiteQuery
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
import com.example.maxwell.models.Study
import com.example.maxwell.utils.activities.forms.StudyFormActivityTests
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getStudySubjectsForTests
import com.example.maxwell.utils.hasError
import com.example.maxwell.utils.hasLength
import com.example.maxwell.utils.parseDate
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class AddStudyFormActivityTests: StudyFormActivityTests() {
    private val studySubjects = getStudySubjectsForTests()

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
            studySubjectDao.insert(*studySubjects)
        }
    }

    @Test
    fun shouldDisplayTheStudyFormEmptyWhenAddingATask() {
        navigateToAddStudyScreen()

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.title_text_input_label))
            ))

        onView(withId(R.id.description_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.description_text_input_label))
            ))

        onView(withId(R.id.duration_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.duration_text_input_label))
            ))

        onView(withId(R.id.subject_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.subject_text_input_label))
            ))

        onView(withId(R.id.links_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.links_text_input_label))
            ))

        onView(withId(R.id.status_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.status_text_input_label))
            ))

        onView(withId(R.id.starting_date_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.starting_date_text_input_label))
            ))

        onView(withId(R.id.save_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.save_button_text))
            ))
    }

    @Test
    fun shouldAddNewStudy() {
        navigateToAddStudyScreen()

        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate,
            subject = subject,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.studies_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(
                    0,
                    Study(
                        title = title,
                        description = description,
                        duration = BigDecimal(duration),
                        links = links,
                        startingDate = parseDate(startingDate),
                        subjectId = subject.id,
                        status = status
                    )
                )
            ))

        val studies = runBlocking {
            studyDao.filterStudies(SimpleSQLiteQuery("SELECT * FROM Study;"))
        }

        assertEquals(studies.size, 1)

        val newStudy = studies[0]

        assertEquals(title, newStudy.title)
        assertEquals(description, newStudy.description)
        assertEquals(BigDecimal(duration), newStudy.duration)
        assertEquals(subject.id, newStudy.subjectId)
        assertEquals(links, newStudy.links)
        assertEquals(status, newStudy.status)
        assertEquals(parseDate(startingDate), newStudy.startingDate)
    }

    @Test
    fun shouldRequireTitle() {
        navigateToAddStudyScreen()

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
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireDuration() {
        navigateToAddStudyScreen()

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
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireStartingDate() {
        navigateToAddStudyScreen()

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
            .check(matches(hasError(R.string.data_format_instruction)))
    }

    @Test
    fun shouldRequireSubject() {
        navigateToAddStudyScreen()

        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate,
            status = status
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.subject_text_input))
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireStatus() {
        navigateToAddStudyScreen()

        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate,
            subject = subject
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.status_text_input))
            .check(matches(hasError(R.string.required_field_error)))
    }

    private fun navigateToAddStudyScreen() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(1, click()))

        onView(withId(R.id.add_fab)).perform(click())
    }
}