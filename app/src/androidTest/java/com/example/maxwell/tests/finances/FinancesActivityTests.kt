package com.example.maxwell.tests.finances

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.hasTextColor
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.adapters.MenuAdapter
import com.example.maxwell.models.Finance
import com.example.maxwell.utils.activities.base.FinanceTests
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.getFinanceCategoriesForTests
import com.example.maxwell.utils.getFinancesForTests
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test

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
        onView(withId(R.id.menu_recycler_view))
            .perform(
                actionOnItemAtPosition<MenuAdapter.ViewHolder>(2, click())
            )

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(0, finances[2])))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(1, finances[0])))

        onView(withId(R.id.finances_recycler_view))
            .check(matches(financeAtPosition(2, finances[1])))

        onView(withId(R.id.add_fab)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_filter)).check(matches(isDisplayed()))
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
}