package com.example.maxwell.activities.studies

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.maxwell.databinding.ActivityStudiesBinding

class StudiesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityStudiesBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val addFab = binding.addFab

        addFab.setOnClickListener {
            val intent = Intent(this@StudiesActivity, StudyFormActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }
}