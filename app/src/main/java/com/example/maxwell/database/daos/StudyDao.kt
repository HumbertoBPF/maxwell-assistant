package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.maxwell.models.Study
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("""
        SELECT original.id, original.title, original.duration, original.description, original.subjectId, original.links, original.status, grouped.startingDate FROM 
        (SELECT * FROM Study ORDER BY startingDate DESC) AS original 
        LEFT JOIN (SELECT * FROM Study GROUP BY startingDate)AS grouped
        ON original.id = grouped.id
    """)
    fun getStudies(): Flow<List<Study>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(study: Study)
}