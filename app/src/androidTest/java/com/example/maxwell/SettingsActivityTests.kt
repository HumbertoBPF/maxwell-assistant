package com.example.maxwell

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test

class SettingsActivityTests: UITests() {

    @Test
    fun shouldRenderTheSettingsForm() {
        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.username_input_hint))
            ))

        onView(withId(R.id.daily_synchronization_switch))
            .check(matches(
                allOf(isDisplayed(), isNotChecked(), withText(R.string.daily_synchronizations_label))
            ))

        onView(withId(R.id.synchronization_time_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), isNotEnabled(), withHint(R.string.synchronization_time_hint))
            ))

        onView(withId(R.id.export_data_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.export_data_button_text))
            ))

        onView(withId(R.id.import_data_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.import_data_button_text))
            ))

        onView(withId(R.id.save_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.save_button_text))
            ))
    }

    @Test
    fun shouldSaveSettingsAndDisplayTheUsernameInTheGreeting() {
        val username = "John Doe"
        val synchronizationTime = "03:00 AM"

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .perform(
                typeText(username), closeSoftKeyboard()
            )

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        fillSynchronizationTime(synchronizationTime)

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.greeting_text_view))
            .check(matches(
                allOf(isDisplayed(), withText("Good morning, $username"))
            ))

        assertUsernameSetting(username)
        assertSynchronizationTimeSetting(synchronizationTime)
    }

    @Test
    fun shouldDisplayTheFormPrePopulatedWithTheCurrentSettings() {
        val username = "Jane Doe"
        val synchronizationTime = "05:00 AM"

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .perform(
                typeText(username), closeSoftKeyboard()
            )

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        fillSynchronizationTime(synchronizationTime)

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withText(username))
            ))

        onView(withId(R.id.daily_synchronization_switch))
            .check(matches(
                allOf(isDisplayed(), isChecked())
            ))

        onView(withId(R.id.synchronization_time_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withText(synchronizationTime))
            ))
    }

    @Test
    fun shouldOverrideCurrentSettings() {
        val username = "Jane Doe"
        val synchronizationTime = "04:00 AM"

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .perform(
                typeText(username), closeSoftKeyboard()
            )

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        fillSynchronizationTime(synchronizationTime)

        onView(withId(R.id.save_button)).perform(click())

        assertUsernameSetting(username)
        assertSynchronizationTimeSetting(synchronizationTime)

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text)).perform(clearText())

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        onView(withId(R.id.save_button)).perform(click())

        assertUsernameSetting("")
        assertSynchronizationTimeSetting(null)
    }

    @Test
    fun shouldRequireSynchronizationTimeIfDailySynchronizationIsEnabled() {
        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        onView(withId(R.id.save_button)).perform(click())

        val timeFormatError = context.getString(R.string.time_format_instruction)

        onView(withId(R.id.synchronization_time_text_input))
            .check(matches(
                hasDescendant(withText(timeFormatError))
            ))
    }

    @Test
    fun shouldValidateFormatOfSynchronizationTime() {
        val invalidTime = "13:00"

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        fillSynchronizationTime(invalidTime)

        onView(withId(R.id.save_button)).perform(click())

        val timeFormatError = context.getString(R.string.time_format_instruction)

        onView(withId(R.id.synchronization_time_text_input))
            .check(matches(
                hasDescendant(withText(timeFormatError))
            ))
    }

    private fun fillSynchronizationTime(synchronizationTime: String) {
        onView(withId(R.id.synchronization_time_text_input_edit_text)).perform(click())

        onView(withId(com.google.android.material.R.id.material_timepicker_cancel_button))
            .perform(click())

        onView(withId(R.id.synchronization_time_text_input_edit_text))
            .perform(
                typeText(synchronizationTime), closeSoftKeyboard()
            )
    }

    private fun assertUsernameSetting(expectedValue: String) {
        val usernameSetting = runBlocking {
            settings.getUsername().first()
        }
        assertEquals(usernameSetting, expectedValue)
    }

    private fun assertSynchronizationTimeSetting(expectedValue: String?) {
        val synchronizationTimeSetting = runBlocking {
            settings.getDailySynchronizationTime().first()
        }
        assertEquals(synchronizationTimeSetting, expectedValue)
    }
}