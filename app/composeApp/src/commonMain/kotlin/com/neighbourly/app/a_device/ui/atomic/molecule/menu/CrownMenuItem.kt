package com.neighbourly.app.a_device.ui.atomic.molecule.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.signal
import org.jetbrains.compose.resources.painterResource

@Composable
fun CrownMenuItem(
    modifier: Modifier = Modifier,
    isOnline: Boolean = true,
    onClick: () -> Unit
) {
    val cornerShape = RoundedCornerShape(
        bottomStart = 20.dp,
        bottomEnd = 20.dp,
    )
    Box(
        modifier = modifier.alpha(.7f).height(36.dp).width(48.dp)
            .clickable {
                onClick()
            }
            .border(1.dp, AppColors.primary, cornerShape).background(
                color = Color.White,
                shape = cornerShape,
            ),
    ) {
        Image(
            modifier = Modifier.size(36.dp).align(Alignment.Center),
            painter = painterResource(Res.drawable.signal),
            colorFilter = ColorFilter.tint(if (isOnline) AppColors.primary else AppColors.complementary),
            contentDescription = null,
        )
    }
}