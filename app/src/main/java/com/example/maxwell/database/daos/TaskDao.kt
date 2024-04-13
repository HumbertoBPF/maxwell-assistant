package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.maxwell.models.Priority
import com.example.maxwell.models.Status
import com.example.maxwell.models.Task
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
abstract class TaskDao {
    @Query("SELECT * FROM Task WHERE timestampModified >= :lastBackupTimestamp")
    abstract fun getForBackup(lastBackupTimestamp: Long): Flow<List<Task>>

    @Query("""
        SELECT original.id, original.title, original.duration, grouped.dueDate, original.priority, original.status, original.deleted, original.timestampModified FROM 
        (SELECT * FROM Task ORDER BY dueDate DESC) AS original 
        LEFT JOIN (SELECT * FROM Task GROUP BY dueDate) AS grouped
        ON original.id = grouped.id
        WHERE original.deleted = 0
    """)
    abstract fun getAll(): Flow<List<Task>>

    @RawQuery
    abstract suspend fun filter(query: SupportSQLiteQuery): List<Task>

    @Query("SELECT * FROM task WHERE id=:id")
    abstract fun getById(id: Long): Flow<Task?>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(vararg task: Task)

    @Query("DELETE FROM Task WHERE deleted = 1")
    abstract suspend fun deleteAfterBackup()

    suspend fun filter(
        title: String,
        dueDate: Date?,
        priority: Priority?,
        status: Status?
    ): List<Task> {
        var filter = "title LIKE '%' || ? || '%'"
        val args = mutableListOf<Any>(title)

        filter = addDueDateFilter(dueDate, filter, args)
        filter = addPriorityFilter(priority, filter, args)
        filter = addStatusFilter(status, filter, args)

        filter = """
            SELECT original.id, original.title, original.duration, grouped.dueDate, original.priority, original.status, original.deleted, original.timestampModified FROM 
            (SELECT * FROM Task WHERE $filter ORDER BY dueDate DESC) AS original 
            LEFT JOIN (SELECT * FROM Task WHERE $filter GROUP BY dueDate) AS grouped 
            ON original.id = grouped.id
            WHERE original.deleted = 0;
        """.trimIndent()
        args.addAll(args)

        val query = SimpleSQLiteQuery(filter, args.toTypedArray())

        return filter(query)
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