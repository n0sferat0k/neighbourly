package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.WebContentTemplate
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun WebContentPage(
    navigation: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val navState by navigation.state.collectAsState()

    WebContentTemplate(navState.webContent)
}
