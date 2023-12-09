package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.FinanceCategory
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.onEach

class FinanceCategoryRepository(context: Context) {
    private val dao = AppDatabase.instantiate(context).financeCategoryDao()

    suspend fun getFinanceCategories(onPostExecute: (List<FinanceCategory>) -> Unit) {
        dao.getFinanceCategories().onEach {
            IdlingResource.increment()
        }.collect { finances ->
            onPostExecute(finances)
            IdlingResource.decrement()
        }
    }

    suspend fun getFinanceCategoryById(id: Long, onPostExecute: (FinanceCategory?) -> Unit) {
        dao.getFinanceCategoryById(id).onEach {
            IdlingResource.increment()
        }.collect { finance ->
            onPostExecute(finance)
            IdlingResource.decrement()
        }
    }

    suspend fun getFinanceCategoryByName(name: String): FinanceCategory? {
        IdlingResource.increment()
        val financeCategory = dao.getFinanceCategoryByName(name)
        IdlingResource.decrement()
        return financeCategory
    }

    suspend fun insert(financeCategory: FinanceCategory) {
        IdlingResource.increment()
        dao.insert(financeCategory)
        IdlingResource.decrement()
    }

    suspend fun delete(financeCategory: FinanceCategory) {
        IdlingResource.increment()
        dao.delete(financeCategory)
        IdlingResource.decrement()
    }
}