package com.example.maxwell.activities.studies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.maxwell.databinding.ActivityStudyDetailBinding

class StudyDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityStudyDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}