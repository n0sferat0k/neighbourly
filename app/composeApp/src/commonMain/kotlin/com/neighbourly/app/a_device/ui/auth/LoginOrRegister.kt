package com.neighbourly.app.a_device.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.utils.BoxFooter
import com.neighbourly.app.a_device.ui.utils.BoxHeader
import com.neighbourly.app.a_device.ui.utils.BoxScrollableContent
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.register
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginOrRegister() {
    var index by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start))

        BoxScrollableContent(modifier = Modifier.weight(1f)) {
            when (index) {
                0 -> LoginView()
                1 -> Register()
            }
        }
        BoxFooter {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.End)
            ) {
                FriendlyText(
                    modifier = Modifier
                        .clickable(onClick = {
                            index = 0
                        }),
                    text = stringResource(Res.string.login),
                    bold = index == 0
                )
                FriendlyText(
                    modifier = Modifier
                        .clickable(onClick = {
                            index = 1
                        }),
                    text = stringResource(Res.string.register),
                    bold = index == 1
                )
            }
        }
    }
}
