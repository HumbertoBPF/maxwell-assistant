package com.example.maxwell.activities.tasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.databinding.ActivityTaskDetailBinding
import com.example.maxwell.models.Task
import com.example.maxwell.utils.formatDate
import kotlinx.coroutines.launch

class TaskDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTaskDetailBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskDao = AppDatabase.instantiate(this@TaskDetailActivity).taskDao()
        val id = intent.getLongExtra("id", -1)

        lifecycleScope.launch {
            taskDao.getTaskById(id).collect {task ->
                if (task == null) {
                    finish()
                } else {
                    bind(task)
                }
            }
        }

        setContentView(binding.root)
    }

    private fun bind(task: Task) {
        val titleTextView = binding.titleTextView
        titleTextView.text = task.title

        val descriptionTextView = binding.descriptionTextView
        descriptionTextView.text = task.description

        val durationTextView = binding.durationTextView
        durationTextView.text = "${task.duration} h"

        val dueToTextView = binding.dueToTextView

        val dueDate = task.dueDate

        dueDate?.let {
            dueToTextView.text = formatDate(dueDate)
        }

        val priorityIconImageView = binding.priorityIconImageView

        val priorityIconResource = task.priority?.iconResource

        priorityIconResource?.let {
            priorityIconImageView.setImageResource(priorityIconResource)
        }

        val priorityTextView = binding.priorityTextView

        val priorityStringResource = task.priority?.stringResource

        priorityStringResource?.let {
            priorityTextView.text = getString(priorityStringResource)
        }

        val statusIconImageView = binding.statusIconImageView

        val statusIconResource = task.status?.iconResource

        statusIconResource?.let {
            statusIconImageView.setImageResource(statusIconResource)
        }

        val statusTextView = binding.statusTextView

        val statusStringResource = task.status?.stringResource

        statusStringResource?.let {
            statusTextView.text = getString(statusStringResource)
        }
    }
}