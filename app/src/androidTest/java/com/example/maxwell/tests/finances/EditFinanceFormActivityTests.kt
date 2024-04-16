package com.example.maxwell.tests.finances

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
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
import com.example.maxwell.utils.activities.forms.FinanceFormActivityTests
import com.example.maxwell.utils.formatDateForInput
import com.example.maxwell.utils.getFinanceCategoriesForTests
import com.example.maxwell.utils.getFinancesForTests
import com.example.maxwell.utils.getRandomElement
import com.example.maxwell.utils.hasError
import com.example.maxwell.utils.parseDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.util.Calendar

class EditFinanceFormActivityTests: FinanceFormActivityTests() {
    private val financeCategories = getFinanceCategoriesForTests()
    private val finances = getFinancesForTests()

    private val selectedFinance = getRandomElement(finances)

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
            financeDao.insert(*finances)
        }
    }

    @Test
    fun shouldDisplayTheFinanceFormFilledWithDataOfTheSelectedFinance() {
        navigateToEditFinanceFormActivity()

        onView(withId(R.id.title_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.title_text_input_label),
                    withText(selectedFinance.title)
                )
            ))

        val financeCategory = runBlocking {
            financeCategoryDao.getById(selectedFinance.categoryId).first()
        }

        onView(withId(R.id.category_text_input_auto_complete))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.category_text_input_label),
                    withText(financeCategory?.name)
                )
            ))

        onView(withId(R.id.value_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.value_text_input_label),
                    withText("${selectedFinance.value}")
                )
            ))

        val currency = selectedFinance.currency
        val currencyText = currency?.stringResource ?: -1

        onView(withText(currencyText))
            .check(matches(
                allOf(isDisplayed(), isChecked())
            ))

        val financeType = selectedFinance.type
        val financeTypeText = financeType?.stringResource ?: -1

        onView(withText(financeTypeText))
            .check(matches(
                allOf(isDisplayed(), isChecked())
            ))

        val date = selectedFinance.date ?: throw NullPointerException("Date should not be null")

        onView(withId(R.id.date_text_input_edit_text))
            .check(matches(
                allOf(
                    isDisplayed(),
                    withHint(R.string.date_text_input_label),
                    withText(formatDateForInput(date))
                )
            ))

        onView(withId(R.id.save_button))
            .check(matches(
                allOf(isDisplayed(), withText(R.string.save_button_text))
            ))
    }

    @Test
    fun shouldUpdateTheSelectedFinance() {
        val startTimestamp = Calendar.getInstance().timeInMillis

        navigateToEditFinanceFormActivity()

        fillFinanceForm(
            title = title,
            category = category,
            value = value,
            currency = currency,
            financeType = financeType,
            date = date
        )

        onView(withId(R.id.save_button)).perform(click())

        val endTimestamp = Calendar.getInstance().timeInMillis

        assertFinanceDetail(
            Finance(
                title = title,
                categoryId = category.id,
                value = BigDecimal(value),
                currency = currency,
                type = financeType,
                date = parseDate(date)
            )
        )

        val updatedFinance = runBlocking {
            financeDao.getById(selectedFinance.id).first()
        }

        assertEquals(title, updatedFinance?.title)
        assertEquals(category.id, updatedFinance?.categoryId)
        assertEquals(BigDecimal(value), updatedFinance?.value)
        assertEquals(currency, updatedFinance?.currency)
        assertEquals(financeType, updatedFinance?.type)
        assertEquals(parseDate(date), updatedFinance?.date)
        assertTrue(updatedFinance?.timestampModified in startTimestamp..endTimestamp)
        assertEquals(false, updatedFinance?.deleted)
    }

    @Test
    fun shouldRequireTitle() {
        navigateToEditFinanceFormActivity()

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
    fun shouldRequireValue() {
        navigateToEditFinanceFormActivity()

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
        navigateToEditFinanceFormActivity()

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

    private fun navigateToEditFinanceFormActivity() {
        onView(withId(R.id.menu_recycler_view))
            .perform(actionOnItemAtPosition<MenuAdapter.ViewHolder>(2, click()))

        onView(withText(selectedFinance.title)).perform(click())

        onView(withId(R.id.edit_item)).perform(click())
    }
}