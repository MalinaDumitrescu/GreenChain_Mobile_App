package com.greenchain.feature.scan.data

import android.graphics.Bitmap
import com.greenchain.feature.scan.di.RoboFlowApiKey
import com.greenchain.feature.scan.di.RoboFlowModelId
import com.greenchain.feature.scan.util.toJpegRequestBody
import okhttp3.MultipartBody
import javax.inject.Inject

class ScanRepository @Inject constructor(
    private val api: RoboFlowApi,
    @RoboFlowModelId private val modelId: String,
    @RoboFlowApiKey private val apiKey: String
) {

    /** Returns true if an `sgr_logo` is detected with confidence >= [minConfidence]. */
    suspend fun verifySgrLogo(
        cropped: Bitmap,
        minConfidence: Float = 0.6f
    ): Boolean {
        val body = cropped.toJpegRequestBody()
        val part = MultipartBody.Part.createFormData(
            name = "file",
            filename = "scan.jpg",
            body = body
        )

        val response = api.detectLogo(
            modelId = modelId,
            file = part,
            apiKey = apiKey
        )

        return response.predictions.any {
            it.clazz == "sgr_logo" && it.confidence >= minConfidence
        }
    }
}
