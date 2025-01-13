package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

@Composable
fun HalfCircleHalo(modifier: Modifier = Modifier, colors: List<Color> = listOf(
    Color.White,
    Color.White,
    Color.Transparent
)) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawIntoCanvas { canvas ->
            drawRect(
                brush =
                Brush.radialGradient(
                    colors = colors,
                    center = Offset(canvasWidth / 2, canvasHeight),
                    radius = canvasWidth / 2,
                ),
                topLeft = Offset(0f, 0f),
                size = Size(canvasWidth, canvasHeight),
            )
        }
    }
}