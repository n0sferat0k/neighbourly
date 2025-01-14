package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.lato
import org.jetbrains.compose.resources.Font

@Composable
fun FriendlyErrorText(errMsg: String) {
    Text(
        text = errMsg,
        color = AppColors.error,
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
        ),
    )
}