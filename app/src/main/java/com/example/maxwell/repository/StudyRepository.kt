package com.example.maxwell.repository

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.Study
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.onEach

class StudyRepository(context: Context) {
    private val dao = AppDatabase.instantiate(context).studyDao()

    suspend fun getStudies(onPostExecute: (List<Study>) -> Unit) {
        dao.getStudies().onEach {
            IdlingResource.increment()
        }.collect { studies ->
            onPostExecute(studies)
            IdlingResource.decrement()
        }
    }

    suspend fun getStudyById(id: Long, onPostExecute: (Study?) -> Unit) {
        dao.getStudyById(id).onEach {
            IdlingResource.increment()
        }.collect { study ->
            onPostExecute(study)
            IdlingResource.decrement()
        }
    }

    suspend fun filterStudies(query: SimpleSQLiteQuery): List<Study> {
        IdlingResource.increment()
        val filteredStudies = dao.filterStudies(query)
        IdlingResource.decrement()
        return filteredStudies
    }

    suspend fun insert(study: Study) {
        IdlingResource.increment()
        dao.insert(study)
        IdlingResource.decrement()
    }

    suspend fun delete(study: Study) {
        IdlingResource.increment()
        dao.delete(study)
        IdlingResource.decrement()
    }
}