package com.example.maxwell.utils

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.example.maxwell.R
import com.example.maxwell.models.Status
import com.example.maxwell.models.StudySubject
import org.hamcrest.CoreMatchers

open class StudyFormActivityTests: UITests() {
    protected val studyDao = db.studyDao()
    protected val studySubjectDao = db.studySubjectDao()

    protected fun fillStudyForm(
        title: String,
        description: String,
        duration: String,
        links: String,
        startingDate: String,
        subject: StudySubject,
        status: Status
    ) {
        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate
        )

        onView(ViewMatchers.withId(R.id.subject_text_input_auto_complete))
            .perform(ViewActions.click())

        onData(CoreMatchers.`is`(subject.name))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())

        onView(ViewMatchers.withId(R.id.status_text_input_auto_complete))
            .perform(ViewActions.click())

        onData(CoreMatchers.`is`(status.text))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
    }

    protected fun fillStudyForm(
        title: String,
        description: String,
        duration: String,
        links: String,
        startingDate: String,
        status: Status
    ) {
        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate
        )

        onView(ViewMatchers.withId(R.id.status_text_input_auto_complete))
            .perform(ViewActions.click())

        onData(CoreMatchers.`is`(status.text))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
    }

    protected fun fillStudyForm(
        title: String,
        description: String,
        duration: String,
        links: String,
        startingDate: String,
        subject: StudySubject
    ) {
        fillStudyForm(
            title = title,
            description = description,
            duration = duration,
            links = links,
            startingDate = startingDate
        )

        onView(ViewMatchers.withId(R.id.subject_text_input_auto_complete))
            .perform(ViewActions.click())

        onData(CoreMatchers.`is`(subject.name))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
    }

    private fun fillStudyForm(
        title: String,
        description: String,
        duration: String,
        links: String,
        startingDate: String,
    ) {
        onView(ViewMatchers.withId(R.id.title_text_input_edit_text))
            .perform(
                clearText(),
                typeText(title),
                closeSoftKeyboard()
            )

        onView(ViewMatchers.withId(R.id.description_text_input_edit_text))
            .perform(
                clearText(),
                typeText(description),
                closeSoftKeyboard()
            )

        onView(ViewMatchers.withId(R.id.duration_text_input_edit_text))
            .perform(
                clearText(),
                typeText(duration),
                closeSoftKeyboard()
            )

        onView(ViewMatchers.withId(R.id.links_text_input_edit_text))
            .perform(
                clearText(),
                typeText(links),
                closeSoftKeyboard()
            )

        fillDatePickerInput(R.id.starting_date_text_input_edit_text, startingDate)
    }
}