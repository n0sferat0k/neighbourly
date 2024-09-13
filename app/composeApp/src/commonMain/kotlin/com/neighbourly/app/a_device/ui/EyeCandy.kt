package com.neighbourly.app.a_device.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.app_name
import neighbourly.composeapp.generated.resources.curlzmt
import neighbourly.composeapp.generated.resources.houses
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

data object AppColors {
    val primary: Color = Color(0xFF5BA9AE)
    val primaryLight: Color = Color(0xFFdeedee)
    val complementary: Color = Color(0xffae605b)
    val complementaryLight: Color = Color(0xFFeedfde)
}

@Composable
fun BoxHeader(
    modifier: Modifier = Modifier,
    busy: Boolean = false,
) {
    Row(modifier = modifier.padding(start = 10.dp)) {
        Image(
            modifier = Modifier.size(48.dp).align(Alignment.CenterVertically),
            painter = painterResource(Res.drawable.houses),
            colorFilter = ColorFilter.tint(AppColors.primary),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.align(Alignment.Bottom).padding(start = 5.dp),
            text = stringResource(Res.string.app_name),
            style =
                TextStyle(
                    fontFamily = font(),
                    fontSize = 24.sp,
                    color = AppColors.primary,
                ),
        )
        if (busy) {
            CircularProgressIndicator(
                modifier = Modifier.size(36.dp).align(Alignment.Bottom).padding(start = 5.dp),
                color = AppColors.primary,
            )
        }
    }
}

@Composable
fun BoxContent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier.fillMaxWidth()) {
        Box(modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Box(
                modifier = modifier.fillMaxSize().padding(20.dp),
                content = content,
            )
        }
        Box(
            modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops =
                            arrayOf(
                                0.0f to Color.White,
                                0.05f to Color.Transparent,
                                0.95f to Color.Transparent,
                                1f to Color.White,
                            ),
                    ),
                ),
        )
    }
}

@Composable
fun BoxFooter(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.padding(bottom = 10.dp, end = 20.dp),
        content = content,
    )
}

@Composable
fun font() =
    FontFamily(
        Font(
            Res.font.curlzmt,
            FontWeight.Normal,
            FontStyle.Normal,
        ),
    )

@Composable
fun CurlyText(
    modifier: Modifier = Modifier,
    text: String,
    bold: Boolean = false,
) {
    Text(
        modifier = modifier,
        text = text,
        style =
            TextStyle(
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                fontFamily = font(),
                fontSize = 20.sp,
                color = AppColors.primary,
            ),
    )
}

@Composable
fun ErrorText(errMsg: String) {
    Text(
        text = errMsg,
        color = Color.Red,
        style =
            TextStyle(
                fontFamily = font(),
                fontSize = 18.sp,
            ),
    )
}

@Composable
fun HalfCircleHalo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawIntoCanvas { canvas ->
            drawRect(
                brush =
                    Brush.radialGradient(
                        colors = listOf(Color.White, Color.White, Color.Transparent),
                        center = Offset(canvasWidth / 2, canvasHeight),
                        radius = canvasWidth / 2,
                    ),
                topLeft = Offset(0f, 0f),
                size = Size(canvasWidth, canvasHeight),
            )
        }
    }
}

@Composable
fun ContentBox(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .alpha(.9f)
                .fillMaxSize()
                .padding(20.dp, 20.dp, 20.dp, 100.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .border(1.dp, AppColors.primary, RoundedCornerShape(20.dp))
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                    ),
            content = content,
        )
    }
}
