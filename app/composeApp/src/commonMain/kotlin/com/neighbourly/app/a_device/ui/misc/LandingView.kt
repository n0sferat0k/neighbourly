package com.neighbourly.app.a_device.ui.misc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.utils.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.houses
import org.jetbrains.compose.resources.painterResource

@Composable
fun LandingView() {
    Image(
        painter = painterResource(Res.drawable.houses),
        colorFilter = ColorFilter.tint(AppColors.primary),
        contentDescription = null,
        modifier =
        Modifier
            .width(220.dp)
            .height(160.dp)
    )
}
