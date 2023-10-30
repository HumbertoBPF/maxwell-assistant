package com.example.maxwell.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.maxwell.R

class StudiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        title = getString(R.string.studies_title)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studies)
    }
}