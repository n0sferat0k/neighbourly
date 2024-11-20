package com.neighbourly.app.a_device.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.utils.CurlyButton
import com.neighbourly.app.a_device.ui.utils.ErrorText
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.password
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun Login(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel { KoinProvider.KOIN.get<LoginViewModel>() }
) {
    val state by viewModel.state.collectAsState()
    var username by remember { mutableStateOf("n0sferat0k") }
    var password by remember { mutableStateOf("caca") }

    Column(
        modifier = modifier
            .widthIn(max = 400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(Res.string.username)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(Res.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(0.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        CurlyButton(text = stringResource(Res.string.login), loading = state.loading) {
            viewModel.onLogin(username, password)
        }

        if (state.error.isNotEmpty()) {
            ErrorText(state.error)
        }
    }
}
