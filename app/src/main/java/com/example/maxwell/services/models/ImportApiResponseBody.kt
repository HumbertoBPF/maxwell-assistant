package com.example.maxwell.services.models

import com.example.maxwell.models.Task
import com.google.gson.annotations.SerializedName

class ImportApiResponseBody<E>(
    @SerializedName("items") val items: List<E>,
    @SerializedName("last_evaluated_key") val lastEvaluatedKey: CompositeKey?
)