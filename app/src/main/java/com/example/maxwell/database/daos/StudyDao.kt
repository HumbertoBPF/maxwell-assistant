package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.maxwell.models.Status
import com.example.maxwell.models.Study
import com.example.maxwell.models.StudySubject
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
abstract class StudyDao {
    @Query("SELECT * FROM Study WHERE timestampModified >= :lastBackupTimestamp")
    abstract fun getForBackup(lastBackupTimestamp: Long): Flow<List<Study>>

    @Query("""
        SELECT original.id, original.title, original.duration, original.description, original.subjectId, original.links, original.status, grouped.startingDate, original.deleted, original.timestampModified FROM 
        (SELECT * FROM Study ORDER BY startingDate DESC) AS original 
        LEFT JOIN (SELECT * FROM Study GROUP BY startingDate) AS grouped
        ON original.id = grouped.id
        WHERE original.deleted = 0
        ORDER BY original.startingDate DESC
    """)
    abstract fun getAll(): Flow<List<Study>>

    @RawQuery
    abstract suspend fun filter(query: SupportSQLiteQuery): List<Study>

    @Query("SELECT * FROM Study WHERE id=:id AND deleted = 0")
    abstract fun getById(id: Long): Flow<Study?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(vararg study: Study)

    @Query("DELETE FROM Study WHERE deleted = 1")
    abstract suspend fun deleteAfterBackup()

    suspend fun filter(
        title: String,
        status: Status?,
        startingDate: Date?,
        studySubject: StudySubject?
    ): List<Study> {
        var filter = "title LIKE '%' || ? || '%'"
        val args = mutableListOf<Any>(title)

        filter = addStatusFilter(status, filter, args)
        filter = addStartingDateFilter(startingDate, filter, args)
        filter = addStudySubjectFilter(studySubject, filter , args)

        filter = """
            SELECT original.id, original.title, original.duration, original.description, original.subjectId, original.links, original.status, grouped.startingDate, original.deleted, original.timestampModified FROM 
            (SELECT * FROM Study WHERE $filter ORDER BY startingDate DESC) AS original 
            LEFT JOIN (SELECT * FROM Study WHERE $filter GROUP BY startingDate) AS grouped
            ON original.id = grouped.id
            WHERE original.deleted = 0
            ORDER BY original.startingDate DESC;
        """.trimIndent()
        args.addAll(args)

        val query = SimpleSQLiteQuery(filter, args.toTypedArray())

        return filter(query)
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

    private fun addStartingDateFilter(
        startingDate: Date?,
        filter: String,
        args: MutableList<Any>
    ): String {
        startingDate?.let {
            args.add(it.time)
            return "$filter AND startingDate = ?"
        }

        return filter
    }

    private fun addStudySubjectFilter(
        studySubject: StudySubject?,
        filter: String,
        args: MutableList<Any>
    ): String {
        studySubject?.let {
            args.add(it.id)
            return "$filter AND subjectId = ?"
        }

        return filter
    }
}