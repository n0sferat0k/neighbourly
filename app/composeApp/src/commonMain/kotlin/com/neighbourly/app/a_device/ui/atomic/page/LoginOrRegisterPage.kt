package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.LoginOrRegisterTemplate
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginViewModel
import com.neighbourly.app.b_adapt.viewmodel.auth.RegisterViewModel

@Composable
fun LoginOrRegisterPage(
    loginViewModel: LoginViewModel = viewModel { KoinProvider.KOIN.get<LoginViewModel>() },
    registerViewModel: RegisterViewModel = viewModel { KoinProvider.KOIN.get<RegisterViewModel>() }
) {
    val loginState by loginViewModel.state.collectAsState()
    val registerState by registerViewModel.state.collectAsState()

    LoginOrRegisterTemplate(
        loginState, registerState,
        loginUsername = loginViewModel::updateUsername,
        loginPassword = loginViewModel::updatePassword,
        onLogin = loginViewModel::onLogin,
        onRefreshLogin = loginViewModel::refresh,
        registerUsername = registerViewModel::validateUsername,
        registerFullname = registerViewModel::validateFullname,
        registerEmail = registerViewModel::validateEmail,
        registerPhone = registerViewModel::validatePhone,
        registerPassword = registerViewModel::validatePassword,
        onRegister = registerViewModel::onRegister
    )
}