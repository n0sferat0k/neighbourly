package com.neighbourly.app.a_device.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.auth.LoginOrRegister
import com.neighbourly.app.a_device.ui.profile.Profile
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun MainContent(navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }) {
    val navigation by navigationViewModel.state.collectAsState()
    if (navigation.userLoggedIn) {
        Profile()
    } else {
        LoginOrRegister()
    }
}
