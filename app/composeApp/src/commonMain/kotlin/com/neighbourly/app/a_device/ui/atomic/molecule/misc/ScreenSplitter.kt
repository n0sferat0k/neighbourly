package com.neighbourly.app.a_device.ui.atomic.molecule.misc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors

@Composable
fun ScreenSplitter(
    leftContent: @Composable BoxScope.() -> Unit,
    rightContent: @Composable BoxScope.() -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(440.dp)
                    .fillMaxHeight()
                    .background(AppColors.primaryLight),
            ) {
                leftContent()
            }
            Box(
                modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(AppColors.complementaryLight),
            ) {
                rightContent()
            }
        }
    }
}