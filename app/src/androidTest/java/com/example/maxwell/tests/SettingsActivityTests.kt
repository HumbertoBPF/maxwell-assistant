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
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.utils.activities.base.UITests
import com.example.maxwell.utils.getFinanceCategoriesForTests
import com.example.maxwell.utils.getFinancesForTests
import com.example.maxwell.utils.getStudiesForTests
import com.example.maxwell.utils.getStudySubjectsForTests
import com.example.maxwell.utils.getTasksForTests
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test
import java.util.Calendar

class SettingsActivityTests: UITests() {
    private val appDatabase = AppDatabase.instantiate(context)
    private val financeDao = appDatabase.financeDao()
    private val financeCategoryDao = appDatabase.financeCategoryDao()
    private val studyDao = appDatabase.studyDao()
    private val studySubjectDao = appDatabase.studySubjectDao()
    private val taskDao = appDatabase.taskDao()

    private val finances = getFinancesForTests()
    private val financeCategories = getFinanceCategoriesForTests()
    private val studies = getStudiesForTests()
    private val studySubjects = getStudySubjectsForTests()
    private val tasks = getTasksForTests()

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

    @Test
    fun shouldExportDataWhenThereIsNoModifiedData() {
        val startTimestamp = Calendar.getInstance().timeInMillis

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.export_data_button)).perform(click())

        onView(withText(R.string.successful_export_dialog_title)).check(matches(isDisplayed()))

        val endTimestamp = Calendar.getInstance().timeInMillis

        val lastBackupTimestamp = runBlocking {
            settings.getLastBackupTimestamp().first()
        }

        assertTrue(lastBackupTimestamp in startTimestamp..endTimestamp)
    }

    @Test
    fun shouldExportDataWhenThereIsModifiedData() {
        val financeToDelete = finances[0]
        val financeCategoryToDelete = financeCategories[0]
        val studyToDelete = studies[0]
        val studySubjectToDelete = studySubjects[0]
        val taskToDelete = tasks[0]

        financeToDelete.setToDeleted()
        financeCategoryToDelete.setToDeleted()
        studyToDelete.setToDeleted()
        studySubjectToDelete.setToDeleted()
        taskToDelete.setToDeleted()

        runBlocking {
            financeDao.insert(*finances)
            financeCategoryDao.insert(*financeCategories)
            studyDao.insert(*studies)
            studySubjectDao.insert(*studySubjects)
            taskDao.insert(*tasks)
        }

        val startTimestamp = Calendar.getInstance().timeInMillis

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.export_data_button)).perform(click())

        onView(withText(R.string.successful_export_dialog_title)).check(matches(isDisplayed()))

        val endTimestamp = Calendar.getInstance().timeInMillis

        val lastBackupTimestamp = runBlocking {
            settings.getLastBackupTimestamp().first()
        }

        assertTrue(lastBackupTimestamp in startTimestamp..endTimestamp)
        // Checking that only the records with the flag deleted set to true were deleted after the backup
        val finances = runBlocking {
            financeDao.getAll().first()
        }
        val financeCategories = runBlocking {
            financeCategoryDao.getAll().first()
        }
        val studies = runBlocking {
            studyDao.getAll().first()
        }
        val studySubjects = runBlocking {
            studySubjectDao.getAll().first()
        }
        val tasks = runBlocking {
            taskDao.getAll().first()
        }

        assertEquals(2, finances.size)
        assertEquals(2, financeCategories.size)
        assertEquals(2, studies.size)
        assertEquals(2, studySubjects.size)
        assertEquals(2, tasks.size)

        val deletedFinance = runBlocking {
            financeDao.getById(financeToDelete.id).first()
        }
        val deletedFinanceCategory = runBlocking {
            financeCategoryDao.getById(financeCategoryToDelete.id).first()
        }
        val deletedStudy = runBlocking {
            studyDao.getById(studyToDelete.id).first()
        }
        val deletedStudySubject = runBlocking {
            studySubjectDao.getById(studySubjectToDelete.id).first()
        }
        val deletedTask = runBlocking {
            taskDao.getById(taskToDelete.id).first()
        }

        assertNull(deletedFinance)
        assertNull(deletedFinanceCategory)
        assertNull(deletedStudy)
        assertNull(deletedStudySubject)
        assertNull(deletedTask)
    }

    @Test
    fun shouldDeclineToImportData() {
        runBlocking {
            financeDao.insert(*finances)
            financeCategoryDao.insert(*financeCategories)
            studyDao.insert(*studies)
            studySubjectDao.insert(*studySubjects)
            taskDao.insert(*tasks)
        }

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.import_data_button)).perform(click())

        onView(withText(R.string.confirm_restore_backup_dialog_negative_button)).perform(click())

        val finances = runBlocking {
            financeDao.getAll().first()
        }
        val financeCategories = runBlocking {
            financeCategoryDao.getAll().first()
        }
        val studies = runBlocking {
            studyDao.getAll().first()
        }
        val studySubjects = runBlocking {
            studySubjectDao.getAll().first()
        }
        val tasks = runBlocking {
            taskDao.getAll().first()
        }

        assertEquals(3, finances.size)
        assertEquals(3, financeCategories.size)
        assertEquals(3, studies.size)
        assertEquals(3, studySubjects.size)
        assertEquals(3, tasks.size)
    }

    @Test
    fun shouldImportData() {
        runBlocking {
            financeDao.insert(*finances)
            financeCategoryDao.insert(*financeCategories)
            studyDao.insert(*studies)
            studySubjectDao.insert(*studySubjects)
            taskDao.insert(*tasks)
        }

        onView(withId(R.id.settings_item)).perform(click())

        onView(withId(R.id.import_data_button)).perform(click())

        onView(withText(R.string.confirm_restore_backup_dialog_positive_button)).perform(click())

        onView(withText(R.string.successful_import_dialog_title)).check(matches(isDisplayed()))

        val finances = runBlocking {
            financeDao.getAll().first()
        }
        val financeCategories = runBlocking {
            financeCategoryDao.getAll().first()
        }
        val studies = runBlocking {
            studyDao.getAll().first()
        }
        val studySubjects = runBlocking {
            studySubjectDao.getAll().first()
        }
        val tasks = runBlocking {
            taskDao.getAll().first()
        }

        assertEquals(0, finances.size)
        assertEquals(0, financeCategories.size)
        assertEquals(0, studies.size)
        assertEquals(0, studySubjects.size)
        assertEquals(0, tasks.size)
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