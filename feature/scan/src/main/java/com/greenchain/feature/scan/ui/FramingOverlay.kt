package com.greenchain.feature.scan.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke // ✅ important import

/**
 * Draws a translucent mask with a hollow square and a dashed border.
 * `relLeft`..`relRight` / `relTop`..`relBottom` are in 0f..1f relative to this view.
 */
@Composable
fun FramingOverlay(
    relLeft: Float,
    relTop: Float,
    relRight: Float,
    relBottom: Float
) {
    Canvas(Modifier.fillMaxSize()) {
        val L = size.width * relLeft
        val T = size.height * relTop
        val R = size.width * relRight
        val B = size.height * relBottom
        val rect = Rect(L, T, R, B)

        val dim = Color(0x99000000)

        // Dim everything outside the square (four rectangles)
        // Top band
        drawRect(
            color = dim,
            topLeft = Offset(0f, 0f),
            size = Size(width = size.width, height = rect.top)
        )
        // Left band
        drawRect(
            color = dim,
            topLeft = Offset(0f, rect.top),
            size = Size(width = rect.left, height = rect.height)
        )
        // Right band
        drawRect(
            color = dim,
            topLeft = Offset(rect.right, rect.top),
            size = Size(width = size.width - rect.right, height = rect.height)
        )
        // Bottom band
        drawRect(
            color = dim,
            topLeft = Offset(0f, rect.bottom),
            size = Size(width = size.width, height = size.height - rect.bottom)
        )

        // Dashed white border around the square
        drawRect(
            color = Color.White,
            topLeft = Offset(rect.left, rect.top),
            size = rect.size,
            style = Stroke(
                width = 4f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f))
            )
        )
    }
}
