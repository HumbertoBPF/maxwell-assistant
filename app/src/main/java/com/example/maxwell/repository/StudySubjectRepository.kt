package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.StudySubject
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.onEach

class StudySubjectRepository(context: Context) {
    private val dao = AppDatabase.instantiate(context).studySubjectDao()

    suspend fun getStudySubjects(onPostExecute: (List<StudySubject>) -> Unit) {
        dao.getStudySubjects().onEach {
            IdlingResource.increment()
        }.collect { studySubjects ->
            onPostExecute(studySubjects)
            IdlingResource.decrement()
        }
    }

    suspend fun getStudySubjectById(id: Long, onPostExecute: (StudySubject?) -> Unit) {
        dao.getStudySubjectById(id).onEach {
            IdlingResource.increment()
        }.collect { studySubject ->
            onPostExecute(studySubject)
            IdlingResource.decrement()
        }
    }

    suspend fun getStudySubjectByName(name: String): StudySubject? {
        IdlingResource.increment()
        val studySubject = dao.getStudySubjectByName(name)
        IdlingResource.decrement()
        return studySubject
    }

    suspend fun insert(studySubject: StudySubject) {
        IdlingResource.increment()
        dao.insert(studySubject)
        IdlingResource.decrement()
    }

    suspend fun delete(studySubject: StudySubject) {
        IdlingResource.increment()
        dao.delete(studySubject)
        IdlingResource.decrement()
    }
}