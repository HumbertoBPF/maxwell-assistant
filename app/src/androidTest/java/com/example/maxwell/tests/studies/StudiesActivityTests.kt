package com.example.maxwell.tests.studies

import android.view.View
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.models.Study
import com.example.maxwell.utils.activities.base.StudyTests
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.getStudiesForTests
import com.example.maxwell.utils.getStudySubjectsForTests
import com.example.maxwell.utils.hasLength
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import java.lang.NullPointerException

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
        navigateToTheStudiesActivity()

        onView(withId(R.id.studies_recycler_view)).check(matches(hasLength(3)))

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

    @Test
    fun shouldDisplayFilterStudiesDialog() {
        navigateToTheStudiesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.title_label))
            ))

        onView(withId(R.id.subject_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.subject_label))
            ))

        onView(withId(R.id.status_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.status_label))
            ))

        onView(withId(R.id.starting_date_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.starting_date_label))
            ))
    }

    @Test
    fun shouldFilterStudiesByTitle() {
        val randomStudy = getRandomElement(studies)

        navigateToTheStudiesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .perform(typeText(randomStudy.title), closeSoftKeyboard())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.studies_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(0, randomStudy)
            ))
    }

    @Test
    fun shouldFilterStudiesBySubject() {
        val randomStudy = getRandomElement(studies)

        val randomSubject = runBlocking {
            studySubjectDao.getStudySubjectById(randomStudy.id).first()
        }

        navigateToTheStudiesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.subject_text_input_auto_complete)).perform(click())

        onData(`is`(randomSubject?.name))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.studies_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(0, randomStudy)
            ))
    }

    @Test
    fun shouldFilterStudiesByStatus() {
        val randomStudy = getRandomElement(studies)

        navigateToTheStudiesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.status_text_input_auto_complete)).perform(click())

        onData(`is`(randomStudy.status?.text))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.studies_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(0, randomStudy)
            ))
    }

    @Test
    fun shouldFilterStudiesByStartingDate() {
        navigateToTheStudiesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        fillDatePickerInput(R.id.starting_date_text_input_edit_text, "12-05-2023")

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.studies_recycler_view)).check(matches(hasLength(2)))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(0, studies[1])
            ))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(1, studies[2])
            ))
    }

    @Test
    fun shouldFilterStudiesWithMultipleFilters() {
        val randomStudy = getRandomElement(studies)

        val randomSubject = runBlocking {
            studySubjectDao.getStudySubjectById(randomStudy.id).first()
        }

        navigateToTheStudiesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .perform(typeText(randomStudy.title), closeSoftKeyboard())

        onView(withId(R.id.subject_text_input_auto_complete)).perform(click())

        onData(`is`(randomSubject?.name))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(withId(R.id.status_text_input_auto_complete)).perform(click())

        onData(`is`(randomStudy.status?.text))
            .inRoot(isPlatformPopup())
            .perform(click())

        val startingDate = randomStudy.startingDate
            ?: throw NullPointerException("The starting date should not be null")

        fillDatePickerInput(
            R.id.starting_date_text_input_edit_text,
            formatDateForInput(startingDate)
        )

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.studies_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.studies_recycler_view))
            .check(matches(
                studyAtPosition(0, randomStudy)
            ))
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

    private fun navigateToTheStudiesActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(1, click())
            )
    }
}