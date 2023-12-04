package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.maxwell.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query(
        """
        SELECT original.id, original.title, original.duration, grouped.dueDate, original.priority, original.status FROM 
        (SELECT * FROM Task ORDER BY dueDate DESC, id DESC) AS original 
        LEFT JOIN (SELECT * FROM Task GROUP BY dueDate) AS grouped
        ON original.id = grouped.id
        """
    )
    fun getTasks(): Flow<List<Task>>

    @RawQuery
    suspend fun filterTasks(query: SupportSQLiteQuery): List<Task>

    @Query("SELECT * FROM task WHERE id=:id")
    fun getTaskById(id: Long): Flow<Task?>

    @Insert(onConflict = REPLACE)
    suspend fun insert(vararg task: Task)

    @Delete
    suspend fun delete(task: Task)
}