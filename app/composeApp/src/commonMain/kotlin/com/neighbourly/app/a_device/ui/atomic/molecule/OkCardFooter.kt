package com.neighbourly.app.a_device.ui.atomic.molecule

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

@Composable
fun OkCardFooter(onOk:() -> Unit) {
    FriendlyText(
        modifier = Modifier.clickable {
            onOk()
        },
        text = stringResource(Res.string.ok)
    )
}