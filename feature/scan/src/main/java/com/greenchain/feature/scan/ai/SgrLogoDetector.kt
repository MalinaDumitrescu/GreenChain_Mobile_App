package com.greenchain.feature.scan.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min

class SgrLogoDetector(
    context: Context
) {

    private val inputSize = 512
    private val confThreshold = 0.5f
    private val iouThreshold = 0.45f

    private val interpreter: Interpreter

    init {
        val afd = context.assets.openFd("sgr_logo_detector.tflite")
        val inputStream = afd.createInputStream()
        val modelData = inputStream.readBytes()
        inputStream.close()
        afd.close()

        val buffer = ByteBuffer.allocateDirect(modelData.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(modelData)
        buffer.rewind()

        interpreter = Interpreter(buffer)
        Log.d("SgrLogoDetector", "TFLite model loaded, size=${modelData.size}")
    }

    data class Detection(
        val score: Float,
        val box: RectF
    )

    fun hasSgrLogo(bitmap: Bitmap, minConfidence: Float = confThreshold): Boolean {
        Log.d(
            "SgrLogoDetector",
            "hasSgrLogo() called with bitmap ${bitmap.width}x${bitmap.height}, config=${bitmap.config}"
        )
        val detections = detect(bitmap)
        val ok = detections.any { it.score >= minConfidence }
        Log.d(
            "SgrLogoDetector",
            "hasSgrLogo=$ok, count=${detections.size}, scores=${detections.map { it.score }}"
        )
        return ok
    }

    private fun detect(bitmap: Bitmap): List<Detection> {
        val safe = ensureSupportedBitmap(bitmap)
        val resized = Bitmap.createScaledBitmap(safe, inputSize, inputSize, true)

        // [1, H, W, 3] float32 0..1
        val input = ByteBuffer
            .allocateDirect(1 * inputSize * inputSize * 3 * 4)
            .order(ByteOrder.nativeOrder())

        for (y in 0 until inputSize) {
            for (x in 0 until inputSize) {
                val p = resized.getPixel(x, y)
                input.putFloat(((p shr 16) and 0xFF) / 255f)
                input.putFloat(((p shr 8) and 0xFF) / 255f)
                input.putFloat((p and 0xFF) / 255f)
            }
        }
        input.rewind()

        val outTensor = interpreter.getOutputTensor(0)
        val shape = outTensor.shape()
        Log.d("SgrLogoDetector", "Output shape=${shape.contentToString()}")

        if (shape.size != 3) {
            Log.e("SgrLogoDetector", "Unexpected output rank=${shape.size}, bailing")
            return emptyList()
        }

        val dim0 = shape[0]
        val dim1 = shape[1]
        val dim2 = shape[2]

        if (dim0 != 1) {
            Log.e("SgrLogoDetector", "Unexpected dim0=$dim0, expected 1")
            return emptyList()
        }

        val detections = when {
            // Case 1: your model: [1, 5, N] => channels-first
            dim1 == 5 -> parseChannelsFirst(input, dim2)

            // Case 2: classic YOLOv8 export: [1, N, 6+] => per-detection rows
            else      -> parseDetectionsList(input, dim1, dim2)
        }

        Log.d("SgrLogoDetector", "Detections after NMS=${detections.size}")
        return detections
    }

    /**
     * Format [1, 5, N]: out[0][0] = cx, [0][1] = cy, [0][2] = w, [0][3] = h, [0][4] = conf.
     */
    private fun parseChannelsFirst(input: ByteBuffer, numPred: Int): List<Detection> {
        Log.d("SgrLogoDetector", "Parsing as channels-first [1,5,$numPred]")

        val out = Array(1) { Array(5) { FloatArray(numPred) } }
        interpreter.run(input, out)

        val raw = mutableListOf<Detection>()

        for (i in 0 until numPred) {
            val cx = out[0][0][i] * inputSize
            val cy = out[0][1][i] * inputSize
            val w  = out[0][2][i] * inputSize
            val h  = out[0][3][i] * inputSize
            val score = out[0][4][i]

            if (score < confThreshold) continue

            val x1 = max(0f, cx - w / 2f)
            val y1 = max(0f, cy - h / 2f)
            val x2 = min(inputSize.toFloat(), cx + w / 2f)
            val y2 = min(inputSize.toFloat(), cy + h / 2f)

            // Filtrează box-uri aberante
            if (w > 0 && h > 0 && x2 > x1 && y2 > y1) {
                raw.add(Detection(score, RectF(x1, y1, x2, y2)))
            }
        }

        return nonMaxSuppression(raw, iouThreshold)
    }

    /**
     * Format fallback [1, N, C]: cx, cy, w, h, obj, (cls...)
     */
    private fun parseDetectionsList(input: ByteBuffer, n: Int, valuesPerDet: Int): List<Detection> {
        Log.d("SgrLogoDetector", "Parsing as rows [1,$n,$valuesPerDet]")

        val out = ByteBuffer
            .allocateDirect(1 * n * valuesPerDet * 4)
            .order(ByteOrder.nativeOrder())

        interpreter.run(input, out)
        out.rewind()

        val raw = mutableListOf<Detection>()

        for (i in 0 until n) {
            val cx = out.float * inputSize
            val cy = out.float * inputSize
            val w  = out.float * inputSize
            val h  = out.float * inputSize
            val objConf = out.float
            val clsConf = if (valuesPerDet > 5) out.float else 1.0f
            val score = objConf * clsConf

            // sari peste restul coloanei dacă e nevoie
            repeat(valuesPerDet - 6) { if (out.hasRemaining()) out.float }

            if (score < confThreshold) continue

            val x1 = max(0f, cx - w / 2f)
            val y1 = max(0f, cy - h / 2f)
            val x2 = min(inputSize.toFloat(), cx + w / 2f)
            val y2 = min(inputSize.toFloat(), cy + h / 2f)

            if (w > 0 && h > 0 && x2 > x1 && y2 > y1) {
                raw.add(Detection(score, RectF(x1, y1, x2, y2)))
            }
        }

        return nonMaxSuppression(raw, iouThreshold)
    }

    private fun ensureSupportedBitmap(src: Bitmap): Bitmap {
        return if (src.config != Bitmap.Config.ARGB_8888 || !src.isMutable) {
            Log.d("SgrLogoDetector", "Converting bitmap from ${src.config} to ARGB_8888")
            src.copy(Bitmap.Config.ARGB_8888, false)
        } else src
    }

    private fun nonMaxSuppression(
        detections: List<Detection>,
        iouThreshold: Float
    ): List<Detection> {
        val sorted = detections.sortedByDescending { it.score }.toMutableList()
        val result = mutableListOf<Detection>()

        while (sorted.isNotEmpty()) {
            val best = sorted.removeAt(0)
            result.add(best)

            val it = sorted.iterator()
            while (it.hasNext()) {
                val d = it.next()
                if (iou(best.box, d.box) > iouThreshold) {
                    it.remove()
                }
            }
        }
        return result
    }

    private fun iou(a: RectF, b: RectF): Float {
        val left = max(a.left, b.left)
        val top = max(a.top, b.top)
        val right = min(a.right, b.right)
        val bottom = min(a.bottom, b.bottom)

        val inter = max(0f, right - left) * max(0f, bottom - top)
        if (inter <= 0f) return 0f

        val areaA = (a.right - a.left) * (a.bottom - a.top)
        val areaB = (b.right - b.left) * (b.bottom - b.top)
        return inter / (areaA + areaB - inter)
    }
}
