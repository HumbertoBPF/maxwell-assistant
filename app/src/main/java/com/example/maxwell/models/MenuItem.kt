package com.example.maxwell.models

import androidx.appcompat.app.AppCompatActivity

data class MenuItem(
    val color: Int,
    val icon: Int,
    val name: String,
    val nextActivity: Class<out AppCompatActivity>
)