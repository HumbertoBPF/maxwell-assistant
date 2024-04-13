package com.example.maxwell.database.daos

import androidx.room.Dao
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
    @Query("SELECT * FROM Finance WHERE timestampModified >= :lastBackupTimestamp")
    abstract fun getForBackup(lastBackupTimestamp: Long): Flow<List<Finance>>

    @Query("""
        SELECT original.id, original.title, original.categoryId, original.value, original.currency, original.type, grouped.date, original.deleted, original.timestampModified FROM 
        (SELECT * FROM Finance ORDER BY date DESC) AS original 
        LEFT JOIN (SELECT * FROM Finance GROUP BY date) AS grouped
        ON original.id = grouped.id
        WHERE original.deleted = 0
    """)
    abstract fun getAll(): Flow<List<Finance>>

    @RawQuery
    abstract suspend fun filter(query: SupportSQLiteQuery): List<Finance>

    @Query("SELECT * FROM Finance WHERE id=:id")
    abstract fun getById(id: Long): Flow<Finance?>

    @Insert(onConflict = REPLACE)
    abstract suspend fun insert(vararg finance: Finance)

    @Query("DELETE FROM Finance WHERE deleted = 1")
    abstract suspend fun deleteAfterBackup()

    suspend fun filter(
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
            SELECT original.id, original.title, original.categoryId, original.value, original.currency, original.type, grouped.date, original.deleted, original.timestampModified FROM 
            (SELECT * FROM Finance WHERE $filter ORDER BY date DESC) AS original 
            LEFT JOIN (SELECT * FROM Finance WHERE $filter GROUP BY date) AS grouped
            ON original.id = grouped.id
            WHERE original.deleted = 0;
        """.trimIndent()
        args.addAll(args)

        val query = SimpleSQLiteQuery(filter, args.toTypedArray())

        return filter(query)
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