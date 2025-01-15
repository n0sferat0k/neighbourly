package com.neighbourly.app.a_device.ui.atomic.molecule.card

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.register
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginOrRegisterCardFooter(
    onSelectLogin: () -> Unit,
    onSelectRegister: () -> Unit
) {
    var index by remember { mutableStateOf(0) }
    FriendlyText(
        modifier = Modifier
            .clickable(onClick = {
                onSelectLogin()
                index = 0
            }),
        text = stringResource(Res.string.login),
        bold = index == 0
    )
    FriendlyText(
        modifier = Modifier
            .clickable(onClick = {
                onSelectRegister()
                index = 1
            }),
        text = stringResource(Res.string.register),
        bold = index == 1
    )
}