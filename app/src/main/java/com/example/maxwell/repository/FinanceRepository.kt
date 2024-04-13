package com.example.maxwell.repository

import android.content.Context
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.models.Currency
import com.example.maxwell.models.Finance
import com.example.maxwell.models.FinanceType
import com.example.maxwell.utils.IdlingResource
import kotlinx.coroutines.flow.onEach
import java.util.Date

class FinanceRepository(context: Context) {
    private val dao = AppDatabase.instantiate(context).financeDao()

    suspend fun getFinances(onPostExecute: (List<Finance>) -> Unit) {
        dao.getFinances().onEach {
            IdlingResource.increment()
        }.collect { finances ->
            onPostExecute(finances)
            IdlingResource.decrement()
        }
    }

    suspend fun filterFinances(
        title: String,
        excludeCurrencies: List<Currency>,
        excludeFinanceTypes: List<FinanceType>,
        date: Date?
    ): List<Finance> {
        IdlingResource.increment()
        val finances = dao.filterFinances(title, excludeCurrencies, excludeFinanceTypes, date)
        IdlingResource.decrement()
        return finances
    }

    suspend fun getFinanceById(id: Long, onPostExecute: (Finance?) -> Unit) {
        dao.getFinanceById(id).onEach {
            IdlingResource.increment()
        }.collect { finance ->
            onPostExecute(finance)
            IdlingResource.decrement()
        }
    }

    suspend fun insert(vararg finance: Finance) {
        IdlingResource.increment()
        dao.insert(*finance)
        IdlingResource.decrement()
    }

    suspend fun delete(finance: Finance) {
        IdlingResource.increment()
        dao.delete(finance)
        IdlingResource.decrement()
    }
}