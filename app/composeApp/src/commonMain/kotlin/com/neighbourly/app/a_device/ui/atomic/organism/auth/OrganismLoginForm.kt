package com.neighbourly.app.a_device.ui.atomic.organism.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.auth.LoginViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.password
import neighbourly.composeapp.generated.resources.remember_me
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismLoginForm(
    state: LoginViewModel.LoginViewState,
    updateUsername: (user: String) -> Unit,
    updatePassword: (pass: String) -> Unit,
    onLogin: (remember: Boolean) -> Unit,
) {
    var remember by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .widthIn(max = 400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Username Input
        OutlinedTextField(
            value = state.username,
            onValueChange = { updateUsername(it) },
            label = { Text(stringResource(Res.string.username)) },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = state.password,
            onValueChange = { updatePassword(it) },
            label = { Text(stringResource(Res.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(0.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            FriendlyText(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(Res.string.remember_me)
            )
            Checkbox(
                checked = remember,
                onCheckedChange = { remember = it },
                colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FriendlyButton(text = stringResource(Res.string.login), loading = state.loading) {
            onLogin(remember)
        }

        if (state.error.isNotEmpty()) {
            FriendlyErrorText(state.error)
        }
    }
}
