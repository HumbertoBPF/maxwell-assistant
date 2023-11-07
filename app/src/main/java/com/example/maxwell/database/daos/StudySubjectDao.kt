package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.maxwell.models.StudySubject
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySubjectDao {
    @Query("SELECT * FROM StudySubject ORDER BY id DESC")
    fun getStudySubjects(): Flow<List<StudySubject>>

    @Query("SELECT * FROM StudySubject WHERE name=:name")
    suspend fun getStudySubjectsByName(name: String): StudySubject?

    @Insert
    suspend fun insert(studySubject: StudySubject)

    @Delete
    suspend fun delete(studySubject: StudySubject)
}