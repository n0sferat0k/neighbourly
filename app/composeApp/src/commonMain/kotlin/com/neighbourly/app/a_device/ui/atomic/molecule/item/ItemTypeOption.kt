package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors

@Composable
fun ItemTypeOption(selected: Boolean = false, icon: Painter, contentDesc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(36.dp).let {
            if (selected) {
                it.border(2.dp, AppColors.primary, CircleShape)
            } else it
        },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier =
            Modifier.size(30.dp).clickable {
                onClick()
            },
            painter = icon,
            contentDescription = "Type Image",
            colorFilter = ColorFilter.tint(AppColors.primary),
        )
    }
}