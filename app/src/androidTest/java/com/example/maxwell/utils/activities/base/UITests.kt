package com.example.maxwell.utils.activities.base

import android.content.Context
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.maxwell.activities.MainActivity
import com.example.maxwell.data_store.Settings
import com.example.maxwell.database.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule

abstract class UITests {
    protected val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    protected val settings = Settings(context)
    protected val db = AppDatabase.instantiate(context)

    @get:Rule
    val rule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    open fun setUp() {
        runBlocking {
            settings.setUsername("")
            settings.setDailySynchronizationTime(null)
        }

        db.clearAllTables()
    }

    protected fun fillTimePickerInput(id: Int, value: String) {
        onView(withId(id)).perform(click())

        onView(withId(com.google.android.material.R.id.material_timepicker_cancel_button))
            .perform(click())

        onView(withId(id)).perform(typeText(value), closeSoftKeyboard())
    }

    protected fun fillDatePickerInput(id: Int, value: String) {
        onView(withId(id)).perform(click())

        onView(withId(com.google.android.material.R.id.cancel_button)).perform(click())

        onView(withId(id)).perform(clearText(), typeText(value), closeSoftKeyboard())
    }
}