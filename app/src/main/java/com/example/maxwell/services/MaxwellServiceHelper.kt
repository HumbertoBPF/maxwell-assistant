package com.example.maxwell.services

import com.example.maxwell.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class MaxwellServiceHelper {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val maxwellService = retrofit.create<MaxwellService>()
}