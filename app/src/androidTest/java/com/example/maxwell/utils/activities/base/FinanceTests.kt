package com.example.maxwell.utils.activities.base

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.example.maxwell.R
import com.example.maxwell.models.Finance
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.formatDatePretty
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

open class FinanceTests: UITests() {
    protected val financeCategoryDao = db.financeCategoryDao()
    protected val financeDao = db.financeDao()

    protected fun financeAtPosition(position: Int, finance: Finance): Matcher<in View> {
        val titleMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.title_text_view),
                ViewMatchers.withText(finance.title)
            )
        )

        val expectedValueColor = finance.type?.color ?: -1
        val expectedValue = finance.formatValue()

        val valueMatcher = ViewMatchers.hasDescendant(
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.withId(R.id.value_text_view),
                ViewMatchers.withText(expectedValue),
                ViewMatchers.hasTextColor(expectedValueColor)
            )
        )

        val financeMatcher = CoreMatchers.allOf(
            titleMatcher,
            valueMatcher
        )

        return atPosition(position, financeMatcher)
    }

    protected fun assertFinanceDetail(finance: Finance) {
        Espresso.onView(ViewMatchers.withId(R.id.title_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(finance.title)
                    )
                )
            )

        val category = runBlocking {
            financeCategoryDao.getById(finance.categoryId).first()
        }

        Espresso.onView(ViewMatchers.withId(R.id.category_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(category?.name)
                    )
                )
            )

        val financeTypeColor = finance.type?.color ?: -1

        Espresso.onView(ViewMatchers.withId(R.id.value_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(finance.formatValue()),
                        ViewMatchers.hasTextColor(financeTypeColor)
                    )
                )
            )

        val expectedDate = finance.date ?: throw NullPointerException("The date should not be null")

        Espresso.onView(ViewMatchers.withId(R.id.date_text_view))
            .check(
                ViewAssertions.matches(
                    CoreMatchers.allOf(
                        ViewMatchers.isDisplayed(),
                        ViewMatchers.withText(formatDatePretty(expectedDate))
                    )
                )
            )
    }
}