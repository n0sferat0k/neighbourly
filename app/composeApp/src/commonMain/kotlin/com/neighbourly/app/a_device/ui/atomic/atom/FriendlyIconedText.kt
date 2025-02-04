package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.lato
import org.jetbrains.compose.resources.Font

@Composable
fun FriendlyIconedText(
    modifier: Modifier = Modifier,
    text: String,
    painter: Painter,
    bold: Boolean = false,
    fontSize: TextUnit = 18.sp,
    iconSize: Dp = 18.dp,
    textAlign: TextAlign = TextAlign.Start,
    iconClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = text,
            style =
            TextStyle(
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily(
                    Font(
                        Res.font.lato,
                        FontWeight.Normal,
                        FontStyle.Normal,
                    ),
                ),
                fontSize = fontSize,
                lineHeight = fontSize,
                color = AppColors.primary,
                textAlign = textAlign
            ),
        )
        Image(
            modifier = Modifier.size(iconSize).clickable {
                iconClick()
            },
            colorFilter = ColorFilter.tint(AppColors.primary),
            contentDescription = text, painter = painter
        )
    }
}