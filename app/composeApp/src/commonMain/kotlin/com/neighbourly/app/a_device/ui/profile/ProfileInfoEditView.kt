package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.CurlyButton
import com.neighbourly.app.a_device.ui.ErrorText
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileInfoEditViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.about
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileInfoEditView(viewModel: ProfileInfoEditViewModel = viewModel { KoinProvider.KOIN.get<ProfileInfoEditViewModel>() }) {
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

    // About Input
    OutlinedTextField(
        value = state.aboutOverride ?: state.about,
        onValueChange = {
            viewModel.updateAbout(it)
        },
        maxLines = 5,
        label = { Text(stringResource(Res.string.about)) },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    CurlyButton(
        text = stringResource(Res.string.save),
        loading = state.saving,
    ) {
        viewModel.onSaveProfile()
    }

    if (state.error.isNotEmpty()) {
        ErrorText(state.error)
    }
}
