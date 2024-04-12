package com.example.maxwell.utils

import android.content.Context
import android.util.Log
import com.example.maxwell.repository.FinanceCategoryRepository
import com.example.maxwell.repository.FinanceRepository
import com.example.maxwell.repository.StudyRepository
import com.example.maxwell.repository.StudySubjectRepository
import com.example.maxwell.repository.TaskRepository
import com.example.maxwell.services.MaxwellServiceHelper
import com.example.maxwell.services.models.ExportApiRequestBody
import com.example.maxwell.services.models.ExportApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private var idToken: String? = null

    private var onSuccess: () -> Unit = {}

    private var onFailure: () -> Unit = {}

    suspend fun export(onSuccess: () -> Unit, onFailure: () -> Unit) {
        getIdToken(context) {idToken ->
            Log.i("HELLO", "idToken $idToken")
            this.idToken = idToken
            this.onSuccess = onSuccess
            this.onFailure = onFailure

            lifecycleScope.launch {
                exportFinances()
            }
        }
    }

    private suspend fun exportFinances() {
        financeRepository.getFinances { finances ->
            export("Finance", finances) {
                lifecycleScope.launch {
                    exportFinanceCategories()
                }
            }
        }
    }

    private suspend fun exportFinanceCategories() {
        financeCategoryRepository.getFinanceCategories { financeCategories ->
            export("FinanceCategory", financeCategories) {
                lifecycleScope.launch {
                    exportTasks()
                }
            }
        }
    }

    private suspend fun exportTasks() {
        taskRepository.getTasks { tasks ->
            export("Task", tasks) {
                lifecycleScope.launch {
                    exportStudies()
                }
            }
        }
    }

    private suspend fun exportStudies() {
        studyRepository.getStudies { studies ->
            export("Study", studies) {
                lifecycleScope.launch {
                    exportStudySubjects()
                }
            }
        }
    }

    private suspend fun exportStudySubjects() {
        studySubjectRepository.getStudySubjects { studySubjects ->
            export("StudySubject", studySubjects) {
                lifecycleScope.launch {
                    onSuccess()
                }
            }
        }
    }

    private fun export(
        table: String,
        entities: List<Any>,
        onNextStep: () -> Unit
    ) {
        val requestBody = ExportApiRequestBody(table, entities)
        val exportCall = serviceHelper.export("Bearer $idToken", requestBody)

        exportCall.enqueue(object : Callback<ExportApiResponse?> {
            override fun onResponse(
                call: Call<ExportApiResponse?>,
                response: Response<ExportApiResponse?>
            ) {
                if (response.isSuccessful) {
                    onNextStep()
                    return
                }

                onFailure()
            }

            override fun onFailure(call: Call<ExportApiResponse?>, t: Throwable) {
                onFailure()
            }
        })
    }
}