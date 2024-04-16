package com.example.maxwell.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.utils.activities.base.UITests
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
                allOf(isDisplayed(), isNotChecked(), withText(R.string.daily_backup_label))
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

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .perform(
                typeText(username), closeSoftKeyboard()
            )

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.greeting_text_view))
            .check(matches(
                allOf(isDisplayed(), withText("Good morning, $username"))
            ))

        assertUsernameSetting(username)
        assertSynchronizationTimeSetting(true)
    }

    @Test
    fun shouldDisplayTheFormPrePopulatedWithTheCurrentSettings() {
        val username = "Jane Doe"

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .perform(
                typeText(username), closeSoftKeyboard()
            )

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

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
    }

    @Test
    fun shouldOverrideCurrentSettings() {
        val username = "Jane Doe"

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text))
            .perform(
                typeText(username), closeSoftKeyboard()
            )

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        onView(withId(R.id.save_button)).perform(click())

        assertUsernameSetting(username)
        assertSynchronizationTimeSetting(true)

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.username_text_input_edit_text)).perform(clearText())

        onView(withId(R.id.daily_synchronization_switch)).perform(click())

        onView(withId(R.id.save_button)).perform(click())

        assertUsernameSetting("")
        assertSynchronizationTimeSetting(false)
    }

    private fun assertUsernameSetting(expectedValue: String) {
        val usernameSetting = runBlocking {
            settings.getUsername().first()
        }
        assertEquals(expectedValue, usernameSetting)
    }

    private fun assertSynchronizationTimeSetting(expectedValue: Boolean?) {
        val synchronizationTimeSetting = runBlocking {
            settings.isDailySyncEnabled().first()
        }
        assertEquals(expectedValue, synchronizationTimeSetting)
    }
}