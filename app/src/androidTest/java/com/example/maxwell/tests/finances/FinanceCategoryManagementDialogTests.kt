package com.example.maxwell.tests.finances

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.utils.activities.base.FinanceTests
import com.example.maxwell.utils.closeChip
import com.example.maxwell.utils.getFinanceCategoriesForTests
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.hasError
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FinanceCategoryManagementDialogTests: FinanceTests() {
    private val financeCategories = getFinanceCategoriesForTests()

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            financeCategoryDao.insert(*financeCategories)
        }
    }

    @Test
    fun shouldDisplayFinanceCategoriesOnTheDialog() {
        navigateToTheFinanceForm()

        onView(withId(R.id.manage_finance_categories_text_view)).perform(click())

        onView(withText(financeCategories[2].name)).perform(scrollTo())

        onView(withId(R.id.finance_categories_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(financeCategories[2].name))
                )
            ))

        onView(withText(financeCategories[1].name)).perform(scrollTo())

        onView(withId(R.id.finance_categories_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(financeCategories[1].name))
                )
            ))

        onView(withText(financeCategories[0].name)).perform(scrollTo())

        onView(withId(R.id.finance_categories_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(financeCategories[0].name))
                )
            ))
    }

    @Test
    fun shouldAddFinanceCategory() {
        val newFinanceCategoryName = "New Finance Category Name"

        navigateToTheFinanceForm()

        onView(withId(R.id.manage_finance_categories_text_view)).perform(click())

        onView(withId(R.id.name_text_input_edit_text))
            .perform(typeText(newFinanceCategoryName), closeSoftKeyboard())

        onView(withText(R.string.finance_category_dialog_positive_button)).perform(click())
        // Checking if the created finance category was added to the list displayed on the dialog
        onView(withText(newFinanceCategoryName)).perform(scrollTo())

        onView(withId(R.id.finance_categories_chip_group))
            .check(matches(
                hasDescendant(
                    allOf(isDisplayed(), withText(newFinanceCategoryName))
                )
            ))
        // Checking if a new finance category was created in the database
        val newFinanceCategory = runBlocking {
            financeCategoryDao.getByName(newFinanceCategoryName)
        }

        assertEquals(newFinanceCategoryName, newFinanceCategory?.name)
    }

    @Test
    fun shouldNotAddTheFinanceCategoryIfThereIsACategoryWithTheSameName() {
        val randomFinanceCategory = getRandomElement(financeCategories)

        navigateToTheFinanceForm()

        onView(withId(R.id.manage_finance_categories_text_view)).perform(click())

        onView(withId(R.id.name_text_input_edit_text))
            .perform(typeText(randomFinanceCategory.name), closeSoftKeyboard())

        onView(withText(R.string.finance_category_dialog_positive_button)).perform(click())

        onView(withId(R.id.name_text_input_layout))
            .check(matches(hasError(R.string.error_finance_category_name_unavailable)))
    }

    @Test
    fun shouldDeleteFinanceCategory() {
        val randomFinanceCategory = getRandomElement(financeCategories)

        navigateToTheFinanceForm()

        onView(withId(R.id.manage_finance_categories_text_view)).perform(click())

        onView(withText(randomFinanceCategory.name))
            .perform(scrollTo(), closeChip())

        onView(withId(R.id.finance_categories_chip_group))
            .check(matches(not(
                hasDescendant(
                    allOf(isDisplayed(), withText(randomFinanceCategory.name))
                )
            )))

        val financeCategory = runBlocking {
            financeCategoryDao.getById(randomFinanceCategory.id).first()
        }

        assertNull(financeCategory)
    }

    private fun navigateToTheFinanceForm() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(2, click()))

        onView(withId(R.id.add_fab)).perform(click())
    }
}