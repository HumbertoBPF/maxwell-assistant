package com.example.maxwell.utils

import kotlin.random.Random

fun <E> getRandomElement(items: Array<E>): E {
    val max = items.size

    val randomIndex = Random.nextInt(max)

    return items[randomIndex]
}