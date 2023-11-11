package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.maxwell.models.Finance
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("""
        SELECT original.id, original.title, original.categoryId, original.value, original.currency, original.type, grouped.date FROM 
        (SELECT * FROM Finance ORDER BY date DESC) AS original 
        LEFT JOIN (SELECT * FROM Finance GROUP BY date)AS grouped
        ON original.id = grouped.id
    """)
    fun getFinances(): Flow<List<Finance>>

    @Query("SELECT * FROM Finance WHERE id=:id")
    fun getFinanceById(id: Long): Flow<Finance?>

    @Insert(onConflict = REPLACE)
    suspend fun insert(finance: Finance)

    @Delete
    suspend fun delete(finance: Finance)
}