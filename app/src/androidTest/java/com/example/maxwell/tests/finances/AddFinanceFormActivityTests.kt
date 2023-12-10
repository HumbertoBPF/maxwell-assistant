package com.example.maxwell.tests.finances

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.models.Currency
import com.example.maxwell.models.Finance
import com.example.maxwell.models.FinanceType
import com.example.maxwell.utils.activities.forms.FinanceFormActivityTests
import com.example.maxwell.utils.getFinanceCategoriesForTests
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.hasError
import com.example.maxwell.utils.hasLength
import com.example.maxwell.utils.parseDate
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class AddFinanceFormActivityTests: FinanceFormActivityTests() {
    private val financeCategories = getFinanceCategoriesForTests()

    private val title = "Finance title"
    private val category = getRandomElement(financeCategories)
    private val value = "19.99"
    private val currency= getRandomElement(arrayOf(Currency.BRL, Currency.EUR))
    private val financeType = getRandomElement(arrayOf(FinanceType.INCOME, FinanceType.EXPENSE))
    private val date = "12-05-2023"

    @Before
    override fun setUp() {
        super.setUp()
        runBlocking {
            financeCategoryDao.insert(*financeCategories)
        }
    }

    @Test
    fun shouldDisplayTheFinanceFormEmptyWhenAddingAFinance() {
        navigateToAddFinanceScreen()

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.title_text_input_label))
            ))

        onView(withId(R.id.category_text_input_auto_complete))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.category_text_input_label))
            ))

        onView(withId(R.id.value_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.value_text_input_label))
            ))

        onView(withId(R.id.currency_text_view))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.currency_text_view))
            ))

        onView(withId(R.id.brl_radio_button))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.brl_radio_button),
                    isChecked()
                )
            ))

        onView(withId(R.id.euro_radio_button))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.euro_radio_button),
                    isNotChecked()
                )
            ))

        onView(withId(R.id.type_text_view))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.type_text_view))
            ))

        onView(withId(R.id.income_radio_button))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.income_radio_button),
                    isChecked()
                )
            ))

        onView(withId(R.id.expense_radio_button))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withText(R.string.expense_radio_button),
                    isNotChecked()
                )
            ))

        onView(withId(R.id.date_text_input_edit_text))
            .check(matches(
                allOf(isDisplayed(), withHint(R.string.date_text_input_label))
            ))

        onView(withId(R.id.save_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.save_button_text))
            ))
    }

    @Test
    fun shouldAddNewFinance() {
        navigateToAddFinanceScreen()

        fillFinanceForm(
            title = title,
            category = category,
            value = value,
            currency = currency,
            financeType = financeType,
            date = date
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.finances_recycler_view)).check(matches(hasLength(1)))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(
                financeAtPosition(
                    0,
                    Finance(
                        title = title,
                        categoryId = category.id,
                        value = BigDecimal(value),
                        currency = currency,
                        type = financeType,
                        date = parseDate(date)
                    )
                )
            ))

        val finances = runBlocking {
            financeDao.filterFinances(SimpleSQLiteQuery("SELECT * FROM Finance;"))
        }

        assertEquals(1, finances.size)

        val newFinance = finances[0]

        assertEquals(title, newFinance.title)
        assertEquals(category.id, newFinance.categoryId)
        assertEquals(BigDecimal(value), newFinance.value)
        assertEquals(currency, newFinance.currency)
        assertEquals(financeType, newFinance.type)
        assertEquals(parseDate(date), newFinance.date)
    }

    @Test
    fun shouldRequireTitle() {
        navigateToAddFinanceScreen()

        fillFinanceForm(
            title = "",
            category = category,
            value = value,
            currency = currency,
            financeType = financeType,
            date = date
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.title_text_input_layout))
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireCategory() {
        navigateToAddFinanceScreen()

        fillFinanceForm(
            title = title,
            value = value,
            currency = currency,
            financeType = financeType,
            date = date
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.category_text_input))
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireValue() {
        navigateToAddFinanceScreen()

        fillFinanceForm(
            title = title,
            category = category,
            value = "",
            currency = currency,
            financeType = financeType,
            date = date
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.value_text_input_layout))
            .check(matches(hasError(R.string.required_field_error)))
    }

    @Test
    fun shouldRequireDate() {
        navigateToAddFinanceScreen()

        fillFinanceForm(
            title = title,
            category = category,
            value = value,
            currency = currency,
            financeType = financeType,
            date = ""
        )

        onView(withId(R.id.save_button)).perform(click())

        onView(withId(R.id.date_text_input_layout))
            .check(matches(hasError(R.string.data_format_instruction)))
    }

    private fun navigateToAddFinanceScreen() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(2, click()))

        onView(withId(R.id.add_fab)).perform(click())
    }
}