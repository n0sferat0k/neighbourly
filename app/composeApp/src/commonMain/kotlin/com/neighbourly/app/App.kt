package com.neighbourly.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.a_device.ui.utils.AppColors
import com.neighbourly.app.a_device.ui.utils.HalfCircleHalo
import com.neighbourly.app.a_device.ui.MainContent
import com.neighbourly.app.a_device.ui.misc.SignalView
import com.neighbourly.app.a_device.ui.web.WebContentView
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.houses
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    includeMap: Boolean = true,
) {
    val navigation by navigationViewModel.state.collectAsState()

    MaterialTheme {
        requestPermissions()

        if (includeMap) {
            WebContentView(
                modifier = Modifier.fillMaxSize(),
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            SignalView()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter,
        ) {
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
                            navigationViewModel.toggleMainContent()
                        }),
            )

            if (navigation.mainContentVisible) {
                MainContent()
            }
        }
    }
}
