package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.curlzmt
import org.jetbrains.compose.resources.Font

@Composable
fun CurlyText(
    modifier: Modifier = Modifier,
    text: String,
    bold: Boolean = false,
    fontSize: TextUnit = 20.sp,
) {
    Text(
        modifier = modifier,
        text = text,
        style =
        TextStyle(
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily(
                Font(
                    Res.font.curlzmt,
                    FontWeight.Normal,
                    FontStyle.Normal,
                ),
            ),
            fontSize = fontSize,
            color = AppColors.primary,
        ),
    )
}