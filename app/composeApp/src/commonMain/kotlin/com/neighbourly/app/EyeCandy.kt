package com.neighbourly.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.curlzmt
import org.jetbrains.compose.resources.Font

data object AppColors {
    val primary: Color = Color(0xFF5BA9AE)
}

@Composable
fun font() = FontFamily(
    Font(
        Res.font.curlzmt,
        FontWeight.Normal,
        FontStyle.Normal
    )
)

@Composable
fun HalfCircleHalo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawIntoCanvas { canvas ->
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color.White, Color.Transparent),
                    center = Offset(canvasWidth / 2, canvasHeight),
                    radius = canvasWidth / 2,
                ),
                topLeft = Offset(0f, 0f),
                size = Size(canvasWidth, canvasHeight)
            )
        }
    }
}

@Composable
fun ContentBox(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .alpha(.9f)
            .fillMaxSize()
            .padding(20.dp, 20.dp, 20.dp, 100.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(20.dp),
            ),
        content = content
    )
}
