package com.greenchain.feature.scan.data

import android.graphics.Bitmap
import android.util.Log
import com.greenchain.feature.scan.ai.SgrLogoDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    private val detector: SgrLogoDetector
) {

    suspend fun verifySgrLogo(
        cropped: Bitmap,
        minConfidence: Float = 0.5f
    ): Boolean = withContext(Dispatchers.Default) {
        Log.d("ScanRepo", "verifySgrLogo() called")
        val result = detector.hasSgrLogo(cropped, minConfidence)
        Log.d("ScanRepo", "verifySgrLogo() detector result = $result")
        result
    }
}
