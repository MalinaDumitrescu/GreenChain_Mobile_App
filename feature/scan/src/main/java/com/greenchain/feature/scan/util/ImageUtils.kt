package com.greenchain.feature.scan.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlin.math.max
import kotlin.math.min

suspend fun loadBitmap(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= 28) {
        val src = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(src)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}

/** rel rect in [0f,1f], independent of preview size/aspect */
fun cropBitmapByRelativeRect(src: Bitmap, relLeft: Float, relTop: Float, relRight: Float, relBottom: Float): Bitmap {
    val left = (relLeft * src.width).toInt()
    val top = (relTop * src.height).toInt()
    val right = (relRight * src.width).toInt()
    val bottom = (relBottom * src.height).toInt()

    val l = max(0, min(left, src.width - 1))
    val t = max(0, min(top, src.height - 1))
    val r = max(l + 1, min(right, src.width))
    val b = max(t + 1, min(bottom, src.height))

    return Bitmap.createBitmap(src, l, t, r - l, b - t)
}
