package com.example.maxwell.activities.tasks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.maxwell.adapters.TaskAdapter
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.databinding.ActivityTasksBinding
import kotlinx.coroutines.launch

class TasksActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityTasksBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureRecyclerView()
        configureFab()

        setContentView(binding.root)
    }

    private fun configureRecyclerView() {
        lifecycleScope.launch {
            val taskDao = AppDatabase.instantiate(this@TasksActivity).taskDao()

            taskDao.getTasks().collect { tasks ->
                val tasksRecyclerView = binding.tasksRecyclerView
                tasksRecyclerView.adapter = TaskAdapter(this@TasksActivity, tasks)
            }
        }
    }

    private fun configureFab() {
        val addFab = binding.addFab

        addFab.setOnClickListener {
            val intent = Intent(this@TasksActivity, TaskFormActivity::class.java)
            startActivity(intent)
        }
    }
}