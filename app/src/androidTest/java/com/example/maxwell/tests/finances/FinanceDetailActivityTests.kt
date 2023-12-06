package com.example.maxwell.tests.finances

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasTextColor
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.utils.activities.base.FinanceTests
import com.example.maxwell.utils.formatDatePretty
import com.example.maxwell.utils.getFinanceCategoriesForTests
import com.example.maxwell.utils.getFinancesForTests
import com.example.maxwell.utils.getRandomElement
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FinanceDetailActivityTests: FinanceTests() {
    private val financeCategories = getFinanceCategoriesForTests()
    private val finances = getFinancesForTests()

    private val selectedFinance = getRandomElement(finances)

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            financeCategoryDao.insert(*financeCategories)
            financeDao.insert(*finances)
        }
    }

    @Test
    fun shouldDisplayDetailsOfTheSelectedFinance() {
        navigateToTheFinanceDetailActivity()

        onView(withId(R.id.edit_item)).check(matches(isDisplayed()))
        onView(withId(R.id.delete_item)).check(matches(isDisplayed()))

        assertFinanceDetail()
    }

    @Test
    fun shouldDeleteSelectedFinance() {
        navigateToTheFinanceDetailActivity()

        onView(withId(R.id.delete_item)).perform(click())

        onView(withText(R.string.confirm_deletion_dialog_positive_button)).perform(click())

        val finance = runBlocking {
            financeDao.getFinanceById(selectedFinance.id).first()
        }

        assertNull(finance)
    }

    private fun assertFinanceDetail() {
        onView(withId(R.id.title_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(selectedFinance.title)
                    )
                )
            )

        val category = runBlocking {
            financeCategoryDao.getFinanceCategoryById(selectedFinance.categoryId).first()
        }

        onView(withId(R.id.category_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(category?.name)
                    )
                )
            )

        val financeTypeColor = selectedFinance.type?.color ?: -1

        onView(withId(R.id.value_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(selectedFinance.formatValue()),
                        hasTextColor(financeTypeColor)
                    )
                )
            )

        val expectedDate =
            selectedFinance.date ?: throw NullPointerException("The date should not be null")

        onView(withId(R.id.date_text_view))
            .check(
                matches(
                    allOf(
                        isDisplayed(),
                        withText(formatDatePretty(expectedDate))
                    )
                )
            )
    }

    private fun navigateToTheFinanceDetailActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(2, click()))

        onView(withText(selectedFinance.title)).perform(click())
    }
}