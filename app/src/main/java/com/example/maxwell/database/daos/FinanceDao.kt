package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import com.example.maxwell.models.Finance

@Dao
interface FinanceDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(finance: Finance)
}