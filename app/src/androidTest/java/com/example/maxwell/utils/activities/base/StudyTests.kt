package com.example.maxwell.utils.activities.base

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.maxwell.R
import com.example.maxwell.models.Study
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.formatDatePretty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

open class StudyTests: UITests() {
    protected val studyDao = db.studyDao()
    protected val studySubjectDao = db.studySubjectDao()

    protected fun studyAtPosition(position: Int, study: Study): Matcher<in View> {
        val titleMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.title_text_view),
                ViewMatchers.withText(study.title)
            )
        )

        val durationMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.duration_text_view),
                ViewMatchers.withText("${study.duration} h")
            )
        )

        val statusText = context.getString(study.status?.stringResource ?: -1)

        val statusMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.status_text_view),
                ViewMatchers.withText(statusText)
            )
        )

        val studyMatcher = CoreMatchers.allOf(
            titleMatcher,
            durationMatcher,
            statusMatcher
        )

        return atPosition(position, studyMatcher)
    }

    protected fun assertStudyDetails(study: Study) {
        Espresso.onView(ViewMatchers.withId(R.id.title_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(study.title)
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.description_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(study.description)
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.duration_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText("${study.duration} h")
                    )
                )
            )

        val expectedStudySubject = runBlocking {
            studySubjectDao.getStudySubjectById(study.subjectId).first()
        }

        Espresso.onView(ViewMatchers.withId(R.id.subject_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(expectedStudySubject?.name)
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.links_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(study.links)
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.status_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(study.status?.stringResource ?: -1)
                    )
                )
            )

        Espresso.onView(ViewMatchers.withId(R.id.starting_date_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(
                            formatDatePretty(
                                study.startingDate
                                    ?: throw NullPointerException("Starting date should not be null")
                            )
                        )
                    )
                )
            )
    }
}