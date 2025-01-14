package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.BackendInfoTemplate
import com.neighbourly.app.b_adapt.viewmodel.BackendInfoViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun BackendInfoPage(
    viewModel: BackendInfoViewModel = viewModel { KoinProvider.KOIN.get<BackendInfoViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val state by viewModel.state.collectAsState()
    BackendInfoTemplate(state) {
        navigationViewModel.goHome()
    }
}