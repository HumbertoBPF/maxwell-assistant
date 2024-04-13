package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.FinanceCategory
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach

class FinanceCategoryRepository(context: Context) {
    private val dao = AppDatabase.instantiate(context).financeCategoryDao()

    suspend fun getForBackup(lastBackupTimestamp: Long, onPostExecute: (List<FinanceCategory>) -> Unit) {
        dao.getForBackup(lastBackupTimestamp).onEach {
            IdlingResource.increment()
        }.first { finances ->
            onPostExecute(finances)
            IdlingResource.decrement()
            true
        }
    }

    suspend fun getAll(onPostExecute: (List<FinanceCategory>) -> Unit) {
        dao.getAll().onEach {
            IdlingResource.increment()
        }.collect { finances ->
            onPostExecute(finances)
            IdlingResource.decrement()
        }
    }

    suspend fun getById(id: Long, onPostExecute: (FinanceCategory?) -> Unit) {
        dao.getById(id).onEach {
            IdlingResource.increment()
        }.collect { finance ->
            onPostExecute(finance)
            IdlingResource.decrement()
        }
    }

    suspend fun getByName(name: String): FinanceCategory? {
        IdlingResource.increment()
        val financeCategory = dao.getByName(name)
        IdlingResource.decrement()
        return financeCategory
    }

    suspend fun insert(vararg financeCategory: FinanceCategory) {
        IdlingResource.increment()
        dao.insert(*financeCategory)
        IdlingResource.decrement()
    }

    suspend fun delete(financeCategory: FinanceCategory) {
        IdlingResource.increment()
        financeCategory.setToDeleted()
        dao.insert(financeCategory)
        IdlingResource.decrement()
    }

    suspend fun deleteAfterBackup() {
        IdlingResource.increment()
        dao.deleteAfterBackup()
        IdlingResource.decrement()
    }
}