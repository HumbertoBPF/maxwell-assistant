package com.example.maxwell.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.maxwell.activities.tasks.TaskDetailActivity
import com.example.maxwell.databinding.TaskItemBinding
import com.example.maxwell.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskItemAdapter(
    private val context: Context,
    private val tasks: List<Task>
): Adapter<TaskItemAdapter.ViewHolder>() {
    inner class ViewHolder(binding: TaskItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val taskContainer = binding.taskContainer
        private val dueDateTextView = binding.dueDateTextView
        private val titleTextView = binding.titleTextView
        private val durationTextView = binding.durationTextView
        private val priorityIconImageView = binding.priorityIconImageView
        private val priorityTextView = binding.priorityTextView
        private val statusIconImageView = binding.statusIconImageView
        private val statusTextView = binding.statusTextView

        fun bind(task: Task) {
            val dueDate = task.dueDate

            if (dueDate == null) {
                dueDateTextView.visibility = GONE
            } else {
                dueDateTextView.visibility = VISIBLE

                val simpleDateFormat = SimpleDateFormat("LLLL dd, yyyy", Locale.US)
                dueDateTextView.text = simpleDateFormat.format(dueDate)
            }

            titleTextView.text = task.title
            durationTextView.text = "${task.duration} h"

            val priority = task.priority

            val priorityStringResource = priority?.stringResource
            val priorityIconResource = priority?.iconResource

            priorityIconResource?.let {
                priorityIconImageView.setImageResource(priorityIconResource)
            }
            priorityStringResource?.let {
                priorityTextView.text = context.getString(priorityStringResource)
            }

            val status = task.status

            val statusStringResource = status?.stringResource
            val statusIconResource = status?.iconResource

            statusIconResource?.let {
                statusIconImageView.setImageResource(statusIconResource)
            }
            statusStringResource?.let {
                statusTextView.text = context.getString(statusStringResource)
            }

            taskContainer.setOnClickListener {
                val intent = Intent(context, TaskDetailActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = TaskItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)
    }
}