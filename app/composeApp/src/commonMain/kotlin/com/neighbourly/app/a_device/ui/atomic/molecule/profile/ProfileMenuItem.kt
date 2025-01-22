package com.neighbourly.app.a_device.ui.atomic.molecule.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors

@Composable
fun ProfileMenuItem(painter: Painter, error: Boolean, selected: Boolean, onSelect: () -> Unit) {
    Image(
        painter = painter,
        contentDescription = "Menu item",
        contentScale = ContentScale.FillBounds,
        colorFilter =
        if (error) {
            ColorFilter.tint(AppColors.complementary)
        } else {
            ColorFilter.tint(AppColors.primary)
        },
        modifier = Modifier
            .let {
                if (selected)
                    it.border(2.dp, AppColors.primary, CircleShape)
                else it
            }
            .padding(4.dp)
            .size(48.dp).clickable {
                onSelect()
            },
    )
}