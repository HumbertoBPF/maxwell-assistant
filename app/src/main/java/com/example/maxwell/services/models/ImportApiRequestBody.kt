package com.example.maxwell.services.models

import com.google.gson.annotations.SerializedName

class ImportApiRequestBody(
    @SerializedName("table") val table: String,
    @SerializedName("start_key") val startKey: CompositeKey?
)