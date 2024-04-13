package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.StudySubject
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach

class StudySubjectRepository(context: Context) {
    private val dao = AppDatabase.instantiate(context).studySubjectDao()

    suspend fun getForBackup(lastBackupTimestamp: Long, onPostExecute: (List<StudySubject>) -> Unit) {
        dao.getForBackup(lastBackupTimestamp).onEach {
            IdlingResource.increment()
        }.first { studySubjects ->
            onPostExecute(studySubjects)
            IdlingResource.decrement()
            true
        }
    }

    suspend fun getAll(onPostExecute: (List<StudySubject>) -> Unit) {
        dao.getAll().onEach {
            IdlingResource.increment()
        }.collect { studySubjects ->
            onPostExecute(studySubjects)
            IdlingResource.decrement()
        }
    }

    suspend fun getById(id: Long, onPostExecute: (StudySubject?) -> Unit) {
        dao.getById(id).onEach {
            IdlingResource.increment()
        }.collect { studySubject ->
            onPostExecute(studySubject)
            IdlingResource.decrement()
        }
    }

    suspend fun getByName(name: String): StudySubject? {
        IdlingResource.increment()
        val studySubject = dao.getByName(name)
        IdlingResource.decrement()
        return studySubject
    }

    suspend fun insert(vararg studySubject: StudySubject) {
        IdlingResource.increment()
        dao.insert(*studySubject)
        IdlingResource.decrement()
    }

    suspend fun delete(studySubject: StudySubject) {
        IdlingResource.increment()
        studySubject.setToDeleted()
        dao.insert(studySubject)
        IdlingResource.decrement()
    }

    suspend fun deleteAfterBackup() {
        IdlingResource.increment()
        dao.deleteAfterBackup()
        IdlingResource.decrement()
    }
}