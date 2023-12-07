package com.example.maxwell.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.google.android.material.chip.Chip
import org.hamcrest.Matcher

fun closeChip(): ViewAction {
    return object: ViewAction {
        override fun getDescription(): String {
            return "Close the chip component"
        }

        override fun getConstraints(): Matcher<View> {
            return isAssignableFrom(Chip::class.java)
        }

        override fun perform(uiController: UiController?, view: View?) {
            val chip = view as Chip
            chip.performCloseIconClick()
        }
    }
}