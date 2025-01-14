package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
fun FriendlyButton(
    modifier: Modifier = Modifier,
    text: String,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier =
        modifier
            .wrapContentWidth()
            .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier =
                Modifier
                    .size(24.dp)
                    .padding(end = 8.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        }
        Text(
            text = text,
            color = Color.White,
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