package com.neighbourly.app

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.app_name
import neighbourly.composeapp.generated.resources.curlzmt
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource


@Composable
fun Login() {
    Text(
        text = stringResource(Res.string.app_name),
        style = TextStyle(
            fontFamily = FontFamily(Font(Res.font.curlzmt, FontWeight.Normal, FontStyle.Normal)),
            fontSize = 24.sp,
            color = AppColors.primary
        )
    )
}