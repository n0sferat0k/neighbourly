package com.neighbourly.app.a_device.ui.atomic.organism.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.forgot_my_pass
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.password
import neighbourly.composeapp.generated.resources.remember_me
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismLoginForm(
    username: String,
    password: String,
    loading: Boolean,
    error: String,
    onLogin: (username: String, password: String, remember: Boolean) -> Unit,
    onForgot: () -> Unit,
) {
    var remember by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var usernameOverride by remember { mutableStateOf<String?>(null) }
    var passwordOverride by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .widthIn(max = 400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Username Input
        OutlinedTextField(
            value = usernameOverride ?: username,
            onValueChange = { usernameOverride = it },
            label = { Text(stringResource(Res.string.username)) },
            modifier = Modifier.fillMaxWidth(),
        )

        // Password Input
        OutlinedTextField(
            value = passwordOverride ?: password,
            onValueChange = { passwordOverride = it },
            label = { Text(stringResource(Res.string.password)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(0.dp),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

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

        FriendlyButton(text = stringResource(Res.string.login), loading = loading) {
            onLogin(usernameOverride ?: username, passwordOverride ?: password, remember)
        }

        FriendlyText(
            modifier = Modifier.clickable { onForgot() },
            text = stringResource(Res.string.forgot_my_pass),
            fontSize = 20.sp,
            bold = true,
        )

        if (error.isNotEmpty()) {
            FriendlyErrorText(error)
        }
    }
}
