package com.neighbourly.app

import com.neighbourly.app.ui.LoginOrRegister
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.ui.AppColors
import com.neighbourly.app.ui.ContentBox
import com.neighbourly.app.ui.HalfCircleHalo
import com.neighbourly.app.ui.Map
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.houses
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    var tabindex by remember { mutableStateOf(-1) }

    MaterialTheme {
        RequestPermissions()
        Map(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            HalfCircleHalo(
                modifier = Modifier
                    .size(200.dp)
            )
            Image(
                painter = painterResource(Res.drawable.houses),
                colorFilter = ColorFilter.tint(AppColors.primary),
                contentDescription = null,
                modifier = Modifier.width(110.dp).height(80.dp)
                    .clickable(onClick = {
                        tabindex = if (tabindex == 0) -1 else 0
                    })
            )

            AnimatedVisibility(tabindex == 0) {
                ContentBox {
                    LoginOrRegister()
                }
            }
        }
    }
}