package com.example.maxwell.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maxwell.activities.studies.StudyDetailActivity
import com.example.maxwell.databinding.StudyItemBinding
import com.example.maxwell.models.Study
import com.example.maxwell.utils.formatDatePretty

class StudyAdapter(
    private val context: Context,
    private val studies: MutableList<Study>
): RecyclerView.Adapter<StudyAdapter.ViewHolder>(){
    inner class ViewHolder(binding: StudyItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val startingDateTextView = binding.startingDateTextView
        private val studyContainer = binding.studyContainer
        private val titleTextView = binding.titleTextView
        private val durationTextView = binding.durationTextView
        private val descriptionTextView = binding.descriptionTextView
        private val statusIconTextView = binding.statusIconImageView
        private val statusTextView = binding.statusTextView

        fun bind(study: Study) {
            val startingDate = study.startingDate

            if (startingDate == null) {
                startingDateTextView.visibility = GONE
            } else {
                startingDateTextView.visibility = VISIBLE
                startingDateTextView.text = formatDatePretty(startingDate)
            }

            titleTextView.text = study.title
            durationTextView.text = "${study.duration} h"
            descriptionTextView.text = study.description

            val status = study.status
            val statusIconResource = status?.iconResource

            statusIconTextView.setImageResource(statusIconResource ?: 0)

            statusTextView.text = status?.text

            studyContainer.setOnClickListener {
                val intent = Intent(context, StudyDetailActivity::class.java)
                intent.putExtra("id", study.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = StudyItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = studies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val study = studies[position]
        holder.bind(study)
    }

    fun changeDataset(newDataset: List<Study>) {
        studies.clear()
        studies.addAll(newDataset)
        notifyDataSetChanged()
    }
}