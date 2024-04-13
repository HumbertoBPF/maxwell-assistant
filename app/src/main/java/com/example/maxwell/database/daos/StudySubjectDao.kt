package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.maxwell.models.StudySubject
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySubjectDao {
    @Query("SELECT * FROM StudySubject WHERE timestampModified >= :lastBackupTimestamp")
    fun getForBackup(lastBackupTimestamp: Long): Flow<List<StudySubject>>

    @Query("SELECT * FROM StudySubject WHERE deleted = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<StudySubject>>

    @Query("SELECT * FROM StudySubject WHERE name=:name AND deleted = 0")
    suspend fun getByName(name: String): StudySubject?

    @Query("SELECT * FROM StudySubject WHERE id=:id AND deleted = 0")
    fun getById(id: Long): Flow<StudySubject?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg studySubject: StudySubject)

    @Query("DELETE FROM StudySubject WHERE deleted = 1")
    suspend fun deleteAfterBackup()
}