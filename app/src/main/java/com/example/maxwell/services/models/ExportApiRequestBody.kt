package com.example.maxwell.services.models

import com.google.gson.annotations.SerializedName

class ExportApiRequestBody(
    @SerializedName("table") val table: String,
    @SerializedName("items") val items: List<Any>
)
