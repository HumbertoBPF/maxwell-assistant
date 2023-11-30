package com.example.maxwell

import android.content.Context
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.maxwell.activities.MainActivity
import com.example.maxwell.data_store.Settings
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule

abstract class UITests {
    protected val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    protected val settings = Settings(context)

    @Before
    fun setUp() {
        runBlocking {
            settings.setUsername("")
            settings.setDailySynchronizationTime(null)
        }
    }

    @get:Rule
    val rule = ActivityScenarioRule(
        MainActivity::class.java
    )
}