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
import com.example.maxwell.utils.formatDatePretty

class TaskAdapter(
    private val context: Context,
    private var tasks: MutableList<Task>
): Adapter<TaskAdapter.ViewHolder>() {
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
                dueDateTextView.text = formatDatePretty(dueDate)
            }

            titleTextView.text = task.title
            durationTextView.text = "${task.duration} h"

            val priority = task.priority

            val priorityStringResource = priority?.stringResource
            val priorityIconResource = priority?.iconResource

            priorityIconImageView.setImageResource(priorityIconResource ?: 0)
            priorityStringResource?.let {
                priorityTextView.text = context.getString(priorityStringResource)
            }

            val status = task.status

            val statusStringResource = status?.stringResource
            val statusIconResource = status?.iconResource

            statusIconImageView.setImageResource(statusIconResource ?: 0)
            statusStringResource?.let {
                statusTextView.setText(statusStringResource)
            }

            taskContainer.setOnClickListener {
                val intent = Intent(context, TaskDetailActivity::class.java)
                intent.putExtra("id", task.id)
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

    fun changeDataset(newDataset: List<Task>) {
        tasks.clear()
        tasks.addAll(newDataset)
        notifyDataSetChanged()
    }
}