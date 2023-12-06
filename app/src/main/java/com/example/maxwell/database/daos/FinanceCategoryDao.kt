package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.maxwell.models.FinanceCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceCategoryDao {
    @Query("SELECT * FROM FinanceCategory")
    fun getFinanceCategories(): Flow<List<FinanceCategory>>

    @Query("SELECT * FROM FinanceCategory WHERE name=:name")
    suspend fun getFinanceCategoryByName(name: String): FinanceCategory?

    @Query("SELECT * FROM FinanceCategory WHERE id=:id")
    fun getFinanceCategoryById(id: Long): Flow<FinanceCategory?>

    @Insert
    suspend fun insert(vararg financeCategory: FinanceCategory)

    @Delete
    suspend fun delete(financeCategory: FinanceCategory)
}