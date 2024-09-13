package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.ErrorText
import com.neighbourly.app.a_device.ui.font
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileInfoEditViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileInfoEdit(viewModel: ProfileInfoEditViewModel = viewModel { KoinProvider.KOIN.get<ProfileInfoEditViewModel>() }) {
    val state by viewModel.state.collectAsState()

    // Username Input
    OutlinedTextField(
        value = state.username,
        onValueChange = { },
        enabled = false,
        label = { Text(stringResource(Res.string.username)) },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Full Name Input
    OutlinedTextField(
        value = state.fullnameOverride ?: state.fullname,
        onValueChange = {
            viewModel.updateFullname(it)
        },
        label = { Text(stringResource(Res.string.fullname)) },
        isError = state.fullnameError,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Email Input
    OutlinedTextField(
        value = state.emailOverride ?: state.email,
        onValueChange = {
            viewModel.updateEmail(it)
        },
        label = { Text(stringResource(Res.string.email)) },
        isError = state.emailError,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Phone Number Input
    OutlinedTextField(
        value = state.phoneOverride ?: state.phone,
        onValueChange = {
            viewModel.updatePhone(it)
        },
        label = { Text(stringResource(Res.string.phone)) },
        isError = state.phoneError,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Save Button
    Button(
        onClick = {
            viewModel.onSaveProfile()
        },
        modifier =
            Modifier
                .wrapContentWidth()
                .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary),
    ) {
        if (state.saving) {
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
            stringResource(Res.string.save),
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
        ErrorText(state.error)
    }
}
