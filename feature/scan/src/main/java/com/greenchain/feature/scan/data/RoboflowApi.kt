package com.greenchain.feature.scan.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class RoboFlowPrediction(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val confidence: Float,
    @Json(name = "class") val clazz: String
)

@JsonClass(generateAdapter = true)
data class RoboFlowResponse(
    val predictions: List<RoboFlowPrediction>
)

interface RoboFlowApi {

    // POST https://detect.roboflow.com/{modelId}?api_key=...
    @Multipart
    @POST("{modelId}")
    suspend fun detectLogo(
        @Path("modelId") modelId: String,
        @Part file: MultipartBody.Part,
        @Query("api_key") apiKey: String
    ): RoboFlowResponse
}
