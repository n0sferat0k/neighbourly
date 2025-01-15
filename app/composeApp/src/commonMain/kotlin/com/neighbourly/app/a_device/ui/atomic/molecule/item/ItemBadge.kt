package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors

@Composable
fun ItemBadge(imgPainer: Painter, text: String, color: Color = AppColors.primary) {
    Row(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(4.dp))
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Image(
            modifier = Modifier.size(18.dp).align(Alignment.CenterVertically),
            contentScale = ContentScale.Fit,
            painter = imgPainer,
            contentDescription = "Badge Image",
            colorFilter = ColorFilter.tint(color),
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = ": $text",
            color = color,
            fontSize = 12.sp
        )
    }
}