package com.example.maxwell.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.maxwell.utils.BackupManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORK_NAME = "sync_worker"
    }
    override suspend fun doWork(): Result {
        val scope = CoroutineScope(Dispatchers.IO)

        val job = scope.launch {
            val backupManager = BackupManager(context = applicationContext, lifecycleScope = scope)
            backupManager.createBackup({}, {})
        }
        job.join()

        return Result.success()
    }
}