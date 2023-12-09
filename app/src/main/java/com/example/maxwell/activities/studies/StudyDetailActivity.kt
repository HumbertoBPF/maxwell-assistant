package com.example.maxwell.activities.studies

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.databinding.ActivityStudyDetailBinding
import com.example.maxwell.models.Study
import com.example.maxwell.repository.StudyRepository
import com.example.maxwell.repository.StudySubjectRepository
import com.example.maxwell.utils.formatDatePretty
import com.example.maxwell.utils.showConfirmDeletionDialog
import kotlinx.coroutines.launch

class StudyDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityStudyDetailBinding.inflate(layoutInflater)
    }

    private val studyRepository by lazy {
        StudyRepository(this@StudyDetailActivity)
    }

    private val studySubjectRepository by lazy {
        StudySubjectRepository(this@StudyDetailActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getLongExtra("id", 0)

        lifecycleScope.launch {
            studyRepository.getStudyById(id) { study ->
                if (study == null) {
                    finish()
                } else {
                    bind(study)
                    configureAppbarMenu(study)
                }
            }
        }

        setContentView(binding.root)
    }

    private fun configureAppbarMenu(study: Study) {
        val appMenu = binding.appbarMenu

        appMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_item -> {
                    showConfirmDeletionDialog(this@StudyDetailActivity) { _, _ ->
                        lifecycleScope.launch {
                            studyRepository.delete(study)
                            finish()
                        }
                    }
                    true
                }

                R.id.edit_item -> {
                    val intent = Intent(this@StudyDetailActivity, StudyFormActivity::class.java)
                    intent.putExtra("id", study.id)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun bind(study: Study) {
        val titleTextView = binding.titleTextView
        titleTextView.text = study.title

        val descriptionTextView = binding.descriptionTextView
        val description = study.description ?: ""
        descriptionTextView.text = if (description.trim() == "") {
            "---"
        } else {
            description
        }

        val durationTextView = binding.durationTextView
        durationTextView.text = "${study.duration} h"

        lifecycleScope.launch {
            studySubjectRepository.getStudySubjectById(study.subjectId) { studySubject ->
                val subjectTextView = binding.subjectTextView
                subjectTextView.text = studySubject?.name
            }
        }

        val linksTextView = binding.linksTextView
        val links = study.links ?: ""
        linksTextView.text = if (links.trim() == "") {
            "---"
        } else {
            links
        }

        val status = study.status

        val statusIconResource = status?.iconResource
        statusIconResource?.let {
            val statusIconTextView = binding.statusIconImageView
            statusIconTextView.setImageResource(statusIconResource)
        }

        val statusStringResource = status?.stringResource

        statusStringResource?.let {
            val statusTextView = binding.statusTextView
            statusTextView.text = getString(statusStringResource)
        }

        val startingDate = study.startingDate
        startingDate?.let {
            val startingDateTextView = binding.startingDateTextView
            startingDateTextView.text = formatDatePretty(startingDate)
        }
    }
}