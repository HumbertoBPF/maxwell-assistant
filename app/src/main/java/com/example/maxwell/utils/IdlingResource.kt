package com.example.maxwell.utils

import androidx.test.espresso.idling.CountingIdlingResource

class IdlingResource {
    companion object {
        private const val COUNTING_IDLING_RESOURCE_NAME = "Counting Idling Resource"
        @Volatile private var idlingResource: CountingIdlingResource? = null

        fun getIdlingResource(): CountingIdlingResource {
            return idlingResource ?: CountingIdlingResource(COUNTING_IDLING_RESOURCE_NAME).also {
                idlingResource = it
            }
        }

        fun increment() {
            idlingResource?.increment()
        }

        fun decrement() {
            idlingResource?.decrement()
        }
    }
}