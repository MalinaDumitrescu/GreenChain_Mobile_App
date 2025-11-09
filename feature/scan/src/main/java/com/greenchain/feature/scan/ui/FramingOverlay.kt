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
import androidx.compose.ui.graphics.drawscope.Stroke

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

        // Top
        drawRect(
            color = dim,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, rect.top)
        )
        // Left
        drawRect(
            color = dim,
            topLeft = Offset(0f, rect.top),
            size = Size(rect.left, rect.height)
        )
        // Right
        drawRect(
            color = dim,
            topLeft = Offset(rect.right, rect.top),
            size = Size(size.width - rect.right, rect.height)
        )
        // Bottom
        drawRect(
            color = dim,
            topLeft = Offset(0f, rect.bottom),
            size = Size(size.width, size.height - rect.bottom)
        )

        // Border
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
