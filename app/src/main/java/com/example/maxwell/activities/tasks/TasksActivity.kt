package com.example.maxwell.activities.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.adapters.TaskItemAdapter
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.databinding.ActivityTasksBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TasksActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTasksBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val addFab = binding.addFab

        lifecycleScope.launch {
            val taskDao = AppDatabase.instantiate(this@TasksActivity).taskDao()

            taskDao.getTasks().collect {tasks ->
                val tasksRecyclerView = binding.tasksRecyclerView
                tasksRecyclerView.adapter = TaskItemAdapter(this@TasksActivity, tasks)
            }
        }

        addFab.setOnClickListener {
            val intent = Intent(this@TasksActivity, TaskFormActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }
}