package com.example.maxwell.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.maxwell.models.FinanceCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceCategoryDao {
    @Query("SELECT * FROM FinanceCategory WHERE timestampModified >= :lastBackupTimestamp")
    fun getForBackup(lastBackupTimestamp: Long): Flow<List<FinanceCategory>>

    @Query("SELECT * FROM FinanceCategory WHERE deleted = 0")
    fun getAll(): Flow<List<FinanceCategory>>

    @Query("SELECT * FROM FinanceCategory WHERE name=:name AND deleted = 0")
    suspend fun getByName(name: String): FinanceCategory?

    @Query("SELECT * FROM FinanceCategory WHERE id=:id AND deleted = 0")
    fun getById(id: Long): Flow<FinanceCategory?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg financeCategory: FinanceCategory)

    @Query("DELETE FROM FinanceCategory WHERE deleted = 1")
    suspend fun deleteAfterBackup()
}