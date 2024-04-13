package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import java.util.Date

class TaskRepository(context: Context) {
    private val dao by lazy {
        AppDatabase.instantiate(context).taskDao()
    }

    suspend fun getForBackup(lastBackupTimestamp: Long, onPostExecute: (List<Task>) -> Unit) {
        dao.getForBackup(lastBackupTimestamp).onEach {
            IdlingResource.increment()
        }.first { tasks ->
            IdlingResource.decrement()
            onPostExecute(tasks)
            true
        }
    }

    suspend fun getAll(onPostExecute: (List<Task>) -> Unit) {
        dao.getAll().onEach {
            IdlingResource.increment()
        }.collect { tasks ->
            IdlingResource.decrement()
            onPostExecute(tasks)
        }
    }

    suspend fun filter(
        title: String,
        dueDate: Date?,
        priority: Priority?,
        status: Status?
    ): List<Task> {
        IdlingResource.increment()
        val tasks = dao.filter(title, dueDate, priority, status)
        IdlingResource.decrement()
        return tasks
    }

    suspend fun getById(id: Long, onPostExecute: (Task?) -> Unit) {
        dao.getById(id).onEach {
            IdlingResource.increment()
        }.collect { task ->
            IdlingResource.decrement()
            onPostExecute(task)
        }
    }

    suspend fun insert(vararg task: Task) {
        IdlingResource.increment()
        dao.insert(*task)
        IdlingResource.decrement()
    }

    suspend fun delete(task: Task) {
        IdlingResource.increment()
        task.setToDeleted()
        dao.insert(task)
        IdlingResource.decrement()
    }

    suspend fun deleteAfterBackup() {
        IdlingResource.increment()
        dao.deleteAfterBackup()
        IdlingResource.decrement()
    }
}