package com.example.maxwell.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.maxwell.BuildConfig
import com.example.maxwell.database.daos.FinanceCategoryDao
import com.example.maxwell.database.daos.FinanceDao
import com.example.maxwell.database.daos.StudyDao
import com.example.maxwell.database.daos.StudySubjectDao
import com.example.maxwell.database.daos.TaskDao
import com.example.maxwell.models.Finance
import com.example.maxwell.models.FinanceCategory
import com.example.maxwell.models.Study
import com.example.maxwell.models.StudySubject
import com.example.maxwell.models.Task

@Database(
    entities = [Task::class, Study::class, Finance::class, FinanceCategory::class, StudySubject::class],
    version = 2,
    autoMigrations = [AutoMigration (from = 1, to = 2), ],
    exportSchema = true,
    )
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun studyDao(): StudyDao
    abstract fun financeDao(): FinanceDao
    abstract fun studySubjectDao(): StudySubjectDao
    abstract fun financeCategoryDao(): FinanceCategoryDao

    companion object {
        // Applying singleton design pattern when returning the AppDatabase instance
        @Volatile private var db: AppDatabase? = null
        fun instantiate(context: Context): AppDatabase {
            return db ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                BuildConfig.DATABASE_NAME
            ).build().also { db = it }
        }
    }
}