package com.example.maxwell.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun atPosition(position: Int, matcher: Matcher<View>): Matcher<in View> {
    return object: BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            matcher.describeTo(description)
        }

        override fun matchesSafely(item: RecyclerView?): Boolean {
            val viewHolder = item?.findViewHolderForAdapterPosition(position)
                ?: throw IndexOutOfBoundsException("A ViewHolder matching the specified index could not be found");

            return matcher.matches(viewHolder.itemView)
        }
    }
}

fun hasLength(nbOfItems: Int): Matcher<in View>{
    return object: BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description?) {
            description
                ?.appendText("Verifying if the number of items of a RecyclerView is ")
                ?.appendValue(nbOfItems)
        }

        override fun matchesSafely(item: RecyclerView?): Boolean {
            return item?.adapter?.itemCount == nbOfItems
        }

    }
}