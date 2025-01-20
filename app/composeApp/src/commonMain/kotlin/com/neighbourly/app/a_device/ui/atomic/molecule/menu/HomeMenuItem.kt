package com.neighbourly.app.a_device.ui.atomic.molecule.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.HalfCircleHalo
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.houses
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeMenuItem(onClick: () -> Unit) {
    HalfCircleHalo(
        modifier =
        Modifier
            .size(200.dp),
    )
    Image(
        painter = painterResource(Res.drawable.houses),
        colorFilter = ColorFilter.tint(AppColors.primary),
        contentDescription = null,
        modifier =
        Modifier
            .width(110.dp)
            .height(80.dp)
            .clickable(onClick = {
                onClick()
            }),
    )
}