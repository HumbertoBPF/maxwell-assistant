package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.onEach
import java.util.Date

class TaskRepository(context: Context) {
    private val dao by lazy {
        AppDatabase.instantiate(context).taskDao()
    }

    suspend fun getTasks(onPostExecute: (List<Task>) -> Unit) {
        dao.getTasks().onEach {
            IdlingResource.increment()
        }.collect { tasks ->
            IdlingResource.decrement()
            onPostExecute(tasks)
        }
    }

    suspend fun filterTasks(
        title: String,
        dueDate: Date?,
        priority: Priority?,
        status: Status?
    ): List<Task> {
        IdlingResource.increment()
        val tasks = dao.filterTasks(title, dueDate, priority, status)
        IdlingResource.decrement()
        return tasks
    }

    suspend fun getTaskById(id: Long, onPostExecute: (Task?) -> Unit) {
        dao.getTaskById(id).onEach {
            IdlingResource.increment()
        }.collect { task ->
            IdlingResource.decrement()
            onPostExecute(task)
        }
    }

    suspend fun insert(task: Task) {
        IdlingResource.increment()
        dao.insert(task)
        IdlingResource.decrement()
    }

    suspend fun delete(task: Task) {
        IdlingResource.increment()
        dao.delete(task)
        IdlingResource.decrement()
    }
}