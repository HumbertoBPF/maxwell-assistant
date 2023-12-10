package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.maxwell.models.Currency
import com.example.maxwell.models.Finance
import com.example.maxwell.models.FinanceType
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
abstract class FinanceDao {
    @Query("""
        SELECT original.id, original.title, original.categoryId, original.value, original.currency, original.type, grouped.date FROM 
        (SELECT * FROM Finance ORDER BY date DESC) AS original 
        LEFT JOIN (SELECT * FROM Finance GROUP BY date) AS grouped
        ON original.id = grouped.id
    """)
    abstract fun getFinances(): Flow<List<Finance>>

    @RawQuery
    abstract suspend fun filterFinances(query: SupportSQLiteQuery): List<Finance>

    @Query("SELECT * FROM Finance WHERE id=:id")
    abstract fun getFinanceById(id: Long): Flow<Finance?>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(vararg finance: Finance)

    @Delete
    abstract suspend fun delete(finance: Finance)

    suspend fun filterFinances(
        title: String,
        excludeCurrencies: List<Currency>,
        excludeFinanceTypes: List<FinanceType>,
        date: Date?
    ): List<Finance> {
        var filter = "title LIKE '%' || ? || '%'"
        val args = mutableListOf<Any>(title)

        filter = addCurrencyFilter(excludeCurrencies, filter, args)
        filter = addTypeFilter(excludeFinanceTypes, filter, args)
        filter = addDateFilter(date, filter, args)

        filter = """
            SELECT original.id, original.title, original.categoryId, original.value, original.currency, original.type, grouped.date FROM 
            (SELECT * FROM Finance WHERE $filter ORDER BY date DESC) AS original 
            LEFT JOIN (SELECT * FROM Finance WHERE $filter GROUP BY date) AS grouped
            ON original.id = grouped.id;
        """.trimIndent()
        args.addAll(args)

        val query = SimpleSQLiteQuery(filter, args.toTypedArray())

        return filterFinances(query)
    }

    private fun addCurrencyFilter(
        excludeCurrencies: List<Currency>,
        filter: String,
        args: MutableList<Any>
    ): String {
        var queryWithFilter = filter

        excludeCurrencies.forEach { currency ->
            queryWithFilter += " AND currency != ?"
            args.add(currency.text)
        }

        return queryWithFilter
    }

    private fun addTypeFilter(
        excludeFinanceTypes: List<FinanceType>,
        filter: String,
        args: MutableList<Any>
    ): String {
        var queryWithFilter = filter

        excludeFinanceTypes.forEach { financeType ->
            queryWithFilter += " AND type != ?"
            args.add(financeType.text)
        }

        return queryWithFilter
    }

    private fun addDateFilter(
        date: Date?,
        filter: String,
        args: MutableList<Any>
    ): String {
        date?.let {
            args.add(it.time)
            return "$filter AND date = ?"
        }

        return filter
    }
}