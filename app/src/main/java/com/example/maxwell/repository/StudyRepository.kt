package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.Status
import com.example.maxwell.models.Study
import com.example.maxwell.models.StudySubject
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import java.util.Date

class StudyRepository(context: Context) {
    private val dao = AppDatabase.instantiate(context).studyDao()

    suspend fun getForBackup(lastBackupTimestamp: Long, onPostExecute: (List<Study>) -> Unit) {
        dao.getForBackup(lastBackupTimestamp).onEach {
            IdlingResource.increment()
        }.first { studies ->
            onPostExecute(studies)
            IdlingResource.decrement()
            true
        }
    }

    suspend fun getAll(onPostExecute: (List<Study>) -> Unit) {
        dao.getAll().onEach {
            IdlingResource.increment()
        }.collect { studies ->
            onPostExecute(studies)
            IdlingResource.decrement()
        }
    }

    suspend fun getById(id: Long, onPostExecute: (Study?) -> Unit) {
        dao.getById(id).onEach {
            IdlingResource.increment()
        }.collect { study ->
            onPostExecute(study)
            IdlingResource.decrement()
        }
    }

    suspend fun filter(
        title: String,
        status: Status?,
        startingDate: Date?,
        studySubject: StudySubject?
    ): List<Study> {
        IdlingResource.increment()
        val filteredStudies = dao.filter(title, status, startingDate, studySubject)
        IdlingResource.decrement()
        return filteredStudies
    }

    suspend fun insert(vararg study: Study) {
        IdlingResource.increment()
        dao.insert(*study)
        IdlingResource.decrement()
    }

    suspend fun delete(study: Study) {
        IdlingResource.increment()
        study.setToDeleted()
        dao.insert(study)
        IdlingResource.decrement()
    }

    suspend fun deleteAfterBackup() {
        IdlingResource.increment()
        dao.deleteAfterBackup()
        IdlingResource.decrement()
    }
}