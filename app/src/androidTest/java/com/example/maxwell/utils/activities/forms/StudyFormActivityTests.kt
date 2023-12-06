package com.example.maxwell.utils.activities.forms

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers
import com.example.maxwell.R
import com.example.maxwell.models.Status
import com.example.maxwell.models.StudySubject
import com.example.maxwell.utils.activities.base.StudyTests
import org.hamcrest.CoreMatchers.`is`

open class StudyFormActivityTests: StudyTests() {
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
            .perform(click())

        onData(`is`(subject.name))
            .inRoot(isPlatformPopup())
            .perform(click())

        onView(ViewMatchers.withId(R.id.status_text_input_auto_complete))
            .perform(click())

        onData(`is`(status.text))
            .inRoot(isPlatformPopup())
            .perform(click())
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
            .perform(click())

        onData(`is`(status.text))
            .inRoot(isPlatformPopup())
            .perform(click())
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
            .perform(click())

        onData(`is`(subject.name))
            .inRoot(isPlatformPopup())
            .perform(click())
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