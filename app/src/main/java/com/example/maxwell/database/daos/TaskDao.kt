package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
abstract class TaskDao {
    @Query(
        """
        SELECT original.id, original.title, original.duration, grouped.dueDate, original.priority, original.status FROM 
        (SELECT * FROM Task ORDER BY dueDate DESC) AS original 
        LEFT JOIN (SELECT * FROM Task GROUP BY dueDate) AS grouped
        ON original.id = grouped.id
        """
    )
    abstract fun getTasks(): Flow<List<Task>>

    @RawQuery
    abstract suspend fun filterTasks(query: SupportSQLiteQuery): List<Task>

    @Query("SELECT * FROM task WHERE id=:id")
    abstract fun getTaskById(id: Long): Flow<Task?>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(vararg task: Task)

    @Delete
    abstract suspend fun delete(task: Task)

    suspend fun filterTasks(
        title: String,
        dueDate: Date?,
        priority: Priority?,
        status: Status?
    ): List<Task> {
        IdlingResource.increment()

        val query = getFilteringQuery(title, dueDate, priority, status)
        val tasks = filterTasks(query)

        IdlingResource.decrement()

        return tasks
    }

    private fun getFilteringQuery(
        title: String,
        dueDate: Date?,
        priority: Priority?,
        status: Status?,
    ): SimpleSQLiteQuery {
        var filter = "title LIKE '%' || ? || '%'"
        val args = mutableListOf<Any>(title)

        filter = addDueDateFilter(dueDate, filter, args)
        filter = addPriorityFilter(priority, filter, args)
        filter = addStatusFilter(status, filter, args)

        filter = """
            SELECT original.id, original.title, original.duration, grouped.dueDate, original.priority, original.status FROM 
            (SELECT * FROM Task WHERE $filter ORDER BY dueDate DESC) AS original 
            LEFT JOIN (SELECT * FROM Task WHERE $filter GROUP BY dueDate) AS grouped 
            ON original.id = grouped.id;
        """.trimIndent()
        args.addAll(args)

        return SimpleSQLiteQuery(filter, args.toTypedArray())
    }

    private fun addDueDateFilter(
        dueDate: Date?,
        filter: String,
        args: MutableList<Any>
    ): String {
        dueDate?.let {
            args.add(it.time)
            return "$filter AND dueDate = ?"
        }

        return filter
    }

    private fun addPriorityFilter(
        priority: Priority?,
        filter: String,
        args: MutableList<Any>
    ): String {
        priority?.let {
            args.add(it.text)
            return "$filter AND priority = ?"
        }

        return filter
    }

    private fun addStatusFilter(
        status: Status?,
        filter: String,
        args: MutableList<Any>
    ): String {
        status?.let {
            args.add(it.text)
            return "$filter AND status = ?"
        }

        return filter
    }
}