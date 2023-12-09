package com.example.maxwell.tests.finances

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasTextColor
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.models.Currency
import com.example.maxwell.models.Finance
import com.example.maxwell.models.FinanceType
import com.example.maxwell.utils.activities.base.FinanceTests
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getFinanceCategoriesForTests
import com.example.maxwell.utils.getFinancesForTests
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.hasLength
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import java.lang.NullPointerException

class FinancesActivityTests: FinanceTests() {
    private val financeCategories = getFinanceCategoriesForTests()
    private val finances = getFinancesForTests()

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            financeCategoryDao.insert(*financeCategories)
            financeDao.insert(*finances)
        }
    }

    @Test
    fun shouldDisplayFinances() {
        navigateToTheFinancesActivity()

        onView(withId(R.id.finances_recycler_view)).check(matches(hasLength(3)))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(0, finances[2])))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(1, finances[0])))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(2, finances[1])))

        onView(withId(R.id.add_fab)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_filter)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldDisplayFilterFinanceDialog() {
        navigateToTheFinancesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.title_label))
            ))

        onView(withId(R.id.brl_checkbox))
            .check(matches(
                allOf(isDisplayed(), isChecked())
            ))

        onView(withId(R.id.euro_checkbox))
            .check(matches(
                allOf(isDisplayed(), isChecked())
            ))

        onView(withId(R.id.income_checkbox))
            .check(matches(
                allOf(isDisplayed(), isChecked())
            ))

        onView(withId(R.id.expense_checkbox))
            .check(matches(
                allOf(isDisplayed(), isChecked())
            ))

        onView(withId(R.id.date_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.date_label))
            ))
    }

    @Test
    fun shouldFilterFinancesByTitle() {
        val randomFinance = getRandomElement(finances)

        navigateToTheFinancesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .perform(typeText(randomFinance.title), closeSoftKeyboard())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.finances_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(0, randomFinance)))
    }

    @Test
    fun shouldFilterFinancesByCurrency() {
        navigateToTheFinancesActivity()

        onView(withId(R.id.ic_filter)).perform(click())
        // Excluding euro currency from the search query
        onView(withId(R.id.euro_checkbox)).perform(click())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.finances_recycler_view)).check(matches(hasLength(2)))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(0, finances[2])))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(1, finances[1])))
    }

    @Test
    fun shouldFilterFinancesByType() {
        navigateToTheFinancesActivity()

        onView(withId(R.id.ic_filter)).perform(click())
        // Excluding income type from the search query
        onView(withId(R.id.income_checkbox)).perform(click())

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.finances_recycler_view)).check(matches(hasLength(2)))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(0, finances[2])))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(1, finances[0])))
    }

    @Test
    fun shouldFilterFinancesByDate() {
        navigateToTheFinancesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        fillDatePickerInput(R.id.date_text_input_edit_text, "12-05-2023")

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.finances_recycler_view)).check(matches(hasLength(2)))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(0, finances[0])))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(1, finances[1])))
    }

    @Test
    fun shouldFilterFinancesWithMultipleFilters() {
        val randomFinance = getRandomElement(finances)

        navigateToTheFinancesActivity()

        onView(withId(R.id.ic_filter)).perform(click())

        onView(withId(R.id.title_text_input_edit_text))
            .perform(typeText(randomFinance.title), closeSoftKeyboard())

        val otherCurrency = if (randomFinance.currency == Currency.BRL) {
            Currency.EUR.stringResource
        } else {
            Currency.BRL.stringResource
        }

        onView(withText(otherCurrency)).perform(click())

        val otherType = if (randomFinance.type == FinanceType.INCOME) {
            FinanceType.EXPENSE.stringResource
        } else {
            FinanceType.INCOME.stringResource
        }

        onView(withText(otherType)).perform(click())

        val date = randomFinance.date ?: throw NullPointerException("Date should not be null")

        fillDatePickerInput(R.id.date_text_input_edit_text, formatDateForInput(date))

        onView(withText(R.string.search_button)).perform(click())

        onView(withId(R.id.finances_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(0, randomFinance)))
    }

    private fun financeAtPosition(position: Int, finance: Finance): Matcher<in View> {
        val titleMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.title_text_view),
                withText(finance.title)
            )
        )

        val expectedValueColor = finance.type?.color ?: -1
        val expectedValue = finance.formatValue()

        val valueMatcher = hasDescendant(
            allOf(
                isDisplayed(),
                withId(R.id.value_text_view),
                withText(expectedValue),
                hasTextColor(expectedValueColor)
            )
        )

        val financeMatcher = allOf(
            titleMatcher,
            valueMatcher
        )

        return atPosition(position, financeMatcher)
    }

    private fun navigateToTheFinancesActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(2, click())
            )
    }
}