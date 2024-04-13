package com.example.maxwell.services

import com.example.maxwell.models.Finance
import com.example.maxwell.models.FinanceCategory
import com.example.maxwell.models.Study
import com.example.maxwell.models.StudySubject
import com.example.maxwell.models.Task
import com.example.maxwell.services.models.ExportApiRequestBody
import com.example.maxwell.services.models.ExportApiResponseBody
import com.example.maxwell.services.models.ImportApiRequestBody
import com.example.maxwell.services.models.ImportApiResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MaxwellService {
    @POST("export")
    fun export(
        @Header("Authorization") authorization: String,
        @Body requestBody: ExportApiRequestBody
    ): Call<ExportApiResponseBody>

    @POST("import")
    fun importFinances(
        @Header("Authorization") authorization: String,
        @Body requestBody: ImportApiRequestBody
    ): Call<ImportApiResponseBody<Finance>>

    @POST("import")
    fun importFinanceCategories(
        @Header("Authorization") authorization: String,
        @Body requestBody: ImportApiRequestBody
    ): Call<ImportApiResponseBody<FinanceCategory>>

    @POST("import")
    fun importStudies(
        @Header("Authorization") authorization: String,
        @Body requestBody: ImportApiRequestBody
    ): Call<ImportApiResponseBody<Study>>

    @POST("import")
    fun importStudySubjects(
        @Header("Authorization") authorization: String,
        @Body requestBody: ImportApiRequestBody
    ): Call<ImportApiResponseBody<StudySubject>>

    @POST("import")
    fun importTasks(
        @Header("Authorization") authorization: String,
        @Body requestBody: ImportApiRequestBody
    ): Call<ImportApiResponseBody<Task>>
}