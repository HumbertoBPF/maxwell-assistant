package com.example.maxwell.utils.activities.forms

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.maxwell.R
import com.example.maxwell.models.Currency
import com.example.maxwell.models.FinanceCategory
import com.example.maxwell.models.FinanceType
import com.example.maxwell.utils.activities.base.FinanceTests
import org.hamcrest.CoreMatchers.`is`

open class FinanceFormActivityTests: FinanceTests() {
    protected fun fillFinanceForm(
        title: String,
        category: FinanceCategory,
        value: String,
        currency: Currency,
        financeType: FinanceType,
        date: String
    ) {
        fillFinanceForm(
            title = title,
            value = value,
            currency = currency,
            financeType = financeType,
            date = date
        )

        onView(withId(R.id.category_text_input_auto_complete))
            .perform(click())

        onData(`is`(category.name))
            .inRoot(isPlatformPopup())
            .perform(click())
    }

    protected fun fillFinanceForm(
        title: String,
        value: String,
        currency: Currency,
        financeType: FinanceType,
        date: String
    ) {
        onView(withId(R.id.title_text_input_edit_text))
            .perform(
                clearText(),
                typeText(title),
                closeSoftKeyboard()
            )

        onView(withId(R.id.value_text_input_edit_text))
            .perform(
                clearText(),
                typeText(value),
                closeSoftKeyboard()
            )

        val currencyOptionId = if (currency == Currency.BRL) {
            R.id.brl_radio_button
        } else {
            R.id.euro_radio_button
        }

        onView(withId(currencyOptionId)).perform(click())

        onView(ViewMatchers.withText(financeType.text)).perform(click())

        fillDatePickerInput(R.id.date_text_input_edit_text, date)
    }
}