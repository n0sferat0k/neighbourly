package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.lato
import org.jetbrains.compose.resources.Font

@Composable
fun FriendlyAntiButton(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier
            .wrapContentWidth()
            .height(48.dp)
            .border(BorderStroke(1.dp, AppColors.primary), shape = RoundedCornerShape(16.dp)),
    ) {

        Text(
            modifier = Modifier.align(Alignment.Center).padding(start = 4.dp, end = 4.dp),
            text = text,
            color = AppColors.primary,
            style =
            TextStyle(
                fontFamily = FontFamily(
                    Font(
                        Res.font.lato,
                        FontWeight.Normal,
                        FontStyle.Normal,
                    ),
                ),
                fontSize = 18.sp,
                color = AppColors.primary,
            ),
        )
    }
}