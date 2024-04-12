package com.example.maxwell.services

import com.example.maxwell.services.models.ExportApiRequestBody
import com.example.maxwell.services.models.ExportApiResponse
import com.example.maxwell.services.models.ImportApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MaxwellService {
    @POST("export")
    fun export(@Header("Authorization") authorization: String, @Body requestBody: ExportApiRequestBody): Call<ExportApiResponse>

    @POST("import")
    fun import(@Header("Authorization") authorization: String, @Body table: String): Call<ImportApiResponse>
}