package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.LoginOrRegisterTemplate
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginRegisterViewModel

@Composable
fun LoginOrRegisterPage(
    viewModel: LoginRegisterViewModel = viewModel { KoinProvider.KOIN.get<LoginRegisterViewModel>() },

    ) {
    val state by viewModel.state.collectAsState()
    var index by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }


    LaunchedEffect(index) {
        if(index != 2) {
            viewModel.onReset(null)
        }
    }

    LoginOrRegisterTemplate(
        state = state,
        contentIndex = index,
        onLogin = viewModel::onLogin,
        onRegister = viewModel::onRegister,
        onReset = viewModel::onReset,
        onGoToLogin = { index = 0 },
        onGoToRegister = { index = 1 },
        onGoToForgot = { index = 2 }
    )
}