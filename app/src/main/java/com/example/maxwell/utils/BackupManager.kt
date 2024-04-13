package com.example.maxwell.utils

import android.content.Context
import com.example.maxwell.data_store.Settings
import com.example.maxwell.database.AppDatabase
import com.example.maxwell.repository.FinanceCategoryRepository
import com.example.maxwell.repository.FinanceRepository
import com.example.maxwell.repository.StudyRepository
import com.example.maxwell.repository.StudySubjectRepository
import com.example.maxwell.repository.TaskRepository
import com.example.maxwell.services.MaxwellServiceHelper
import com.example.maxwell.services.models.ExportApiRequestBody
import com.example.maxwell.services.models.ExportApiResponseBody
import com.example.maxwell.services.models.ImportApiRequestBody
import com.example.maxwell.services.models.ImportApiResponseBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import kotlin.math.ceil

class BackupManager(
    val context: Context,
    val lifecycleScope: CoroutineScope,
) {
    private val serviceHelper by lazy {
        MaxwellServiceHelper().maxwellService
    }

    private val financeRepository by lazy {
        FinanceRepository(context)
    }

    private val financeCategoryRepository by lazy {
        FinanceCategoryRepository(context)
    }

    private val taskRepository by lazy {
        TaskRepository(context)
    }

    private val studyRepository by lazy {
        StudyRepository(context)
    }

    private val studySubjectRepository by lazy {
        StudySubjectRepository(context)
    }

    private val settings by lazy {
        Settings(context)
    }

    private var idToken: String? = null

    private var lastBackupTimestamp: Long = 0

    private var onSuccess: () -> Unit = {}

    private var onFailure: () -> Unit = {}

    companion object {
        const val FINANCE_CATEGORY_TABLE = "FinanceCategory"
        const val STUDY_SUBJECT_TABLE = "StudySubject"
        const val FINANCE_TABLE = "Finance"
        const val STUDY_TABLE = "Study"
        const val TASK_TABLE = "Task"
        const val BATCH_SIZE = 5.0
    }

    suspend fun createBackup(onSuccess: () -> Unit, onFailure: () -> Unit) {
        getIdToken(context) {idToken ->
            this.idToken = idToken
            this.onSuccess = onSuccess
            this.onFailure = onFailure

            lifecycleScope.launch {
                settings.getLastBackupTimestamp().first {lastBackupTimestamp ->
                    if (lastBackupTimestamp != null) {
                        this@BackupManager.lastBackupTimestamp = lastBackupTimestamp
                    }

                    lifecycleScope.launch {
                        exportFinances()
                    }
                    true
                }
            }
        }
    }

    private suspend fun exportFinances() {
        financeRepository.getForBackup(this.lastBackupTimestamp) { finances ->
            exportBatch(FINANCE_TABLE, finances, 1) {
                lifecycleScope.launch {
                    exportFinanceCategories()
                }
            }
        }
    }

    private suspend fun exportFinanceCategories() {
        financeCategoryRepository.getForBackup(this.lastBackupTimestamp) { financeCategories ->
            exportBatch(FINANCE_CATEGORY_TABLE, financeCategories, 1) {
                lifecycleScope.launch {
                    exportTasks()
                }
            }
        }
    }

    private suspend fun exportTasks() {
        taskRepository.getForBackup(this.lastBackupTimestamp) { tasks ->
            exportBatch(TASK_TABLE, tasks, 1) {
                lifecycleScope.launch {
                    exportStudies()
                }
            }
        }
    }

    private suspend fun exportStudies() {
        studyRepository.getForBackup(this.lastBackupTimestamp) { studies ->
            exportBatch(STUDY_TABLE, studies, 1) {
                lifecycleScope.launch {
                    exportStudySubjects()
                }
            }
        }
    }

    private suspend fun exportStudySubjects() {
        studySubjectRepository.getForBackup(this.lastBackupTimestamp) { studySubjects ->
            exportBatch(STUDY_SUBJECT_TABLE, studySubjects, 1) {
                lifecycleScope.launch {
                    val currentTimestamp = Calendar.getInstance().timeInMillis
                    settings.setLastBackupTimestamp(currentTimestamp)
                    cleanUpAfterBackup()
                    onSuccess()
                }
            }
        }
    }

    private fun <E : Any> exportBatch(
        table: String,
        items: List<E>,
        indexBatch: Int,
        onNextStep: () -> Unit
    ) {
        val nbBatches = ceil(items.size / BATCH_SIZE).toInt()

        if (nbBatches == 0) {
            onNextStep()
            return
        }

        val batch = getBatch(indexBatch, items)

        callExportApi(table, batch) {
            if (indexBatch == nbBatches) {
                onNextStep()
                return@callExportApi
            }

            exportBatch(table, items, indexBatch + 1, onNextStep)
        }
    }

    private fun <E : Any> getBatch(
        indexBatch: Int,
        items: List<E>
    ): MutableList<E> {
        val batch = mutableListOf<E>()

        for (j in 0..4) {
            val index = 5 * (indexBatch - 1) + j

            if (index >= items.size) {
                break
            }

            batch.add(items[index])
        }

        return batch
    }

    private suspend fun cleanUpAfterBackup() {
        financeRepository.deleteAfterBackup()
        studyRepository.deleteAfterBackup()
        taskRepository.deleteAfterBackup()
        financeCategoryRepository.deleteAfterBackup()
        studySubjectRepository.deleteAfterBackup()
    }

    private fun callExportApi(
        table: String,
        entities: List<Any>,
        onNextStep: () -> Unit
    ) {
        val requestBody = ExportApiRequestBody(table, entities)
        val exportCall = serviceHelper.export("Bearer $idToken", requestBody)

        exportCall.enqueue(object : Callback<ExportApiResponseBody?> {
            override fun onResponse(
                call: Call<ExportApiResponseBody?>,
                response: Response<ExportApiResponseBody?>
            ) {
                if (response.isSuccessful) {
                    onNextStep()
                    return
                }

                onFailure()
            }

            override fun onFailure(call: Call<ExportApiResponseBody?>, t: Throwable) {
                onFailure()
            }
        })
    }

    suspend fun restoreBackup(onSuccess: () -> Unit, onFailure: () -> Unit) {
        getIdToken(context) {idToken ->
            this.idToken = idToken
            this.onSuccess = onSuccess
            this.onFailure = onFailure

            lifecycleScope.launch(IO) {
                AppDatabase.instantiate(context).clearAllTables()
                importFinanceCategories()
            }
        }
    }

    private fun importFinanceCategories() {
        val requestBody = ImportApiRequestBody(FINANCE_CATEGORY_TABLE, null)
        val importCall = serviceHelper.importFinanceCategories("Bearer $idToken", requestBody)
        callImportApi(importCall, {items ->
            lifecycleScope.launch {
                financeCategoryRepository.insert(*items.toTypedArray())
            }
        }, {
            importStudySubjects()
        })
    }

    private fun importStudySubjects() {
        val requestBody = ImportApiRequestBody(STUDY_SUBJECT_TABLE, null)
        val importCall = serviceHelper.importStudySubjects("Bearer $idToken", requestBody)
        callImportApi(importCall, {items ->
            lifecycleScope.launch {
                studySubjectRepository.insert(*items.toTypedArray())
            }
        }, {
            importFinances()
        })
    }

    private fun importFinances() {
        val requestBody = ImportApiRequestBody(FINANCE_TABLE, null)
        val importCall = serviceHelper.importFinances("Bearer $idToken", requestBody)
        callImportApi(importCall, {items ->
            lifecycleScope.launch {
                financeRepository.insert(*items.toTypedArray())
            }
        },  {
            importStudies()
        })
    }

    private fun importStudies() {
        val requestBody = ImportApiRequestBody(STUDY_TABLE, null)
        val importCall = serviceHelper.importStudies("Bearer $idToken", requestBody)
        callImportApi(importCall, {items ->
            lifecycleScope.launch {
                studyRepository.insert(*items.toTypedArray())
            }
        }, {
            importTasks()
        })
    }

    private fun importTasks() {
        val requestBody = ImportApiRequestBody(TASK_TABLE, null)
        val importCall = serviceHelper.importTasks("Bearer $idToken", requestBody)
        callImportApi(importCall, {items ->
            lifecycleScope.launch {
                taskRepository.insert(*items.toTypedArray())
            }
        }, {
            onSuccess()
        })
    }

    private fun <E> callImportApi(
        importCall: Call<ImportApiResponseBody<E>>,
        onSave: (List<E>) -> Unit,
        onNextStep: () -> Unit
    ) {
        importCall.enqueue(object : Callback<ImportApiResponseBody<E>?> {
            override fun onResponse(
                call: Call<ImportApiResponseBody<E>?>,
                response: Response<ImportApiResponseBody<E>?>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        val items = responseBody.items
                        val lastEvaluatedKey = responseBody.lastEvaluatedKey

                        onSave(items)

                        if (lastEvaluatedKey != null) {
                            callImportApi(importCall, onSave, onNextStep)
                            return
                        }
                    }

                    onNextStep()
                    return
                }

                onFailure()
            }

            override fun onFailure(call: Call<ImportApiResponseBody<E>?>, t: Throwable) {
                onFailure()
            }
        })
    }
}