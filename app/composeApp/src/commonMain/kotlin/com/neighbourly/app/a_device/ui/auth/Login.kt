package com.neighbourly.app.a_device.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.font
import com.neighbourly.app.b_adapt.viewmodel.LoginViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.password
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun Login(loginViewModel: LoginViewModel = viewModel { KoinProvider.KOIN.get<LoginViewModel>() }) {
    val state by loginViewModel.state.collectAsState()
    var username by remember { mutableStateOf("n0sferat0k") }
    var password by remember { mutableStateOf("caca") }

    Column(
        modifier =
            Modifier
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

        // Register Button
        Button(
            onClick = {
                loginViewModel.onLogin(username, password)
            },
            modifier =
                Modifier
                    .wrapContentWidth()
                    .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary),
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .size(24.dp)
                            .padding(end = 8.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            }
            Text(
                stringResource(Res.string.login),
                color = Color.White,
                style =
                    TextStyle(
                        fontFamily = font(),
                        fontSize = 18.sp,
                        color = AppColors.primary,
                    ),
            )
        }

        if (state.error.isNotEmpty()) {
            Text(
                text = state.error,
                color = Color.Red,
                style =
                    TextStyle(
                        fontFamily = font(),
                        fontSize = 18.sp,
                        color = AppColors.primary,
                    ),
            )
        }
    }
}
