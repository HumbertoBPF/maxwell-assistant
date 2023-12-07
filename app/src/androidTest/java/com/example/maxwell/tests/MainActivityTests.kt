package com.example.maxwell.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.maxwell.R
import com.example.maxwell.utils.activities.base.UITests
import com.example.maxwell.utils.atPosition
import com.example.maxwell.utils.hasLength
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test

class MainActivityTests: UITests() {
    @Test
    fun shouldRenderGreetingMessageAndMenuItems() {
        onView(withId(R.id.greeting_text_view))
            .check(matches(allOf(isDisplayed(), withText("Good morning"))))

        onView(withId(R.id.menu_recycler_view)).check(matches(hasLength(3)))

        onView(withId(R.id.menu_recycler_view))
            .check(matches(
                atPosition(0,
                    hasDescendant(
                        allOf(isDisplayed(), withText(R.string.tasks_title))
                    )
                ))
            )

        onView(withId(R.id.menu_recycler_view))
            .check(matches(
                atPosition(1,
                    hasDescendant(
                        allOf(isDisplayed(), withText(R.string.studies_title))
                    )
                ))
            )

        onView(withId(R.id.menu_recycler_view))
            .check(matches(
                atPosition(2,
                    hasDescendant(
                        allOf(isDisplayed(), withText(R.string.finances_title))
                    )
                ))
            )

        onView(withId(R.id.settings_item)).check(matches(isDisplayed()))
    }
}