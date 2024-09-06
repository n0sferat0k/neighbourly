package com.neighbourly.app.a_device.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.auth.LoginOrRegister
import com.neighbourly.app.a_device.ui.profile.Profile
import com.neighbourly.app.b_adapt.viewmodel.MainViewModel

@Composable
fun MainContent(mainViewModel: MainViewModel = viewModel { KoinProvider.KOIN.get<MainViewModel>() }) {
    val state by mainViewModel.state.collectAsState()
    if (state.isLoggedIn) {
        Profile()
    } else {
        LoginOrRegister()
    }
}
