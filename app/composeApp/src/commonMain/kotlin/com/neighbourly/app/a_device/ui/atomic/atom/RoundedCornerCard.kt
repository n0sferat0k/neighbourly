package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.utils.AppColors

@Composable
fun RoundedCornerCard(
    modifier: Modifier = Modifier.alpha(.9f)
        .fillMaxSize()
        .padding(start = 20.dp, top = 48.dp, end = 20.dp, bottom = 100.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier,
    ) {
        Box(
            modifier =
            Modifier
                .border(1.dp, AppColors.primary, RoundedCornerShape(20.dp))
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                ),
            content = content,
        )
    }
}