package com.neighbourly.app.a_device.ui.atomic.organism.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyAntiButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.d_entity.util.isValidEmail
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.reset_link_sent
import neighbourly.composeapp.generated.resources.send_reset_link
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismForgotForm(
    loading: Boolean,
    error: String,
    resetComplete:Boolean,
    onReset: (email: String) -> Unit,
) {

    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .widthIn(max = 400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(Res.string.email)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !email.isValidEmail()
        )

        if(resetComplete) {
            FriendlyAntiButton(text = stringResource(Res.string.reset_link_sent))
        } else {
            FriendlyButton(text = stringResource(Res.string.send_reset_link), loading = loading) {
                onReset(email)
            }
        }

        if (error.isNotEmpty()) {
            FriendlyErrorText(error)
        }
    }
}
