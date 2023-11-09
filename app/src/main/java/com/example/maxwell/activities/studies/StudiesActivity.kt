package com.example.maxwell.activities.studies

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.adapters.StudyAdapter
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.databinding.ActivityStudiesBinding
import kotlinx.coroutines.launch

class StudiesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityStudiesBinding.inflate(layoutInflater)
    }

    private val studyDao by lazy {
        AppDatabase.instantiate(this@StudiesActivity).studyDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureRecyclerView()
        configureFab()

        setContentView(binding.root)
    }

    private fun configureRecyclerView() {
        lifecycleScope.launch {
            studyDao.getStudies().collect { studies ->
                val recyclerView = binding.studiesRecyclerView
                recyclerView.adapter = StudyAdapter(this@StudiesActivity, studies)
            }
        }
    }

    private fun configureFab() {
        val addFab = binding.addFab

        addFab.setOnClickListener {
            val intent = Intent(this@StudiesActivity, StudyFormActivity::class.java)
            startActivity(intent)
        }
    }
}