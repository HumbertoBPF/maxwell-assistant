package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.maxwell.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query(
        """
        SELECT original.id, original.title, original.duration, grouped.dueDate, original.priority, original.status FROM 
        (SELECT * FROM Task ORDER BY dueDate DESC) AS original 
        LEFT JOIN (SELECT * FROM Task GROUP BY dueDate)AS grouped
        ON original.id = grouped.id
        """
    )
    fun getTasks(): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)
}