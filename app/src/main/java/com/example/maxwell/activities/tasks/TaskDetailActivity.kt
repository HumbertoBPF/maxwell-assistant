package com.example.maxwell.activities.tasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.R
import com.example.maxwell.databinding.ActivityTaskDetailBinding
import com.example.maxwell.models.Task
import com.example.maxwell.repository.TaskRepository
import com.example.maxwell.utils.formatDatePretty
import com.example.maxwell.utils.showConfirmDeletionDialog
import kotlinx.coroutines.launch

class TaskDetailActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTaskDetailBinding.inflate(layoutInflater)
    }

    private val taskRepository by lazy {
        TaskRepository(this@TaskDetailActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val id = intent.getLongExtra("id", 0)

        lifecycleScope.launch {
            taskRepository.getById(id) { task ->
                if (task == null) {
                    finish()
                } else {
                    bind(task)
                    configureAppMenu(task)
                }
            }
        }

        setContentView(binding.root)
    }

    private fun configureAppMenu(task: Task) {
        val appMenu = binding.appbarMenu

        appMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_item -> {
                    showConfirmDeletionDialog(this@TaskDetailActivity) { _, _ ->
                        lifecycleScope.launch {
                            taskRepository.delete(task)
                            finish()
                        }
                    }
                    true
                }

                R.id.edit_item -> {
                    val intent = Intent(this@TaskDetailActivity, TaskFormActivity::class.java)
                    intent.putExtra("id", task.id)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
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
            dueToTextView.text = formatDatePretty(dueDate)
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