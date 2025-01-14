package com.neighbourly.app.a_device.ui.atomic.molecule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.everywhere
import neighbourly.composeapp.generated.resources.here
import neighbourly.composeapp.generated.resources.logout
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogoutCardFooter(onLogout: (logoutAll: Boolean) -> Unit) {
    FriendlyText(text = stringResource(Res.string.logout) + ":")
    FriendlyText(
        modifier =
        Modifier
            .wrapContentWidth()
            .padding(start = 5.dp, end = 5.dp)
            .clickable(onClick = {
                onLogout(false)
            }),
        text = stringResource(Res.string.here),
        bold = true,
    )
    FriendlyText(modifier = Modifier.wrapContentWidth(), text = "/")
    FriendlyText(
        modifier =
        Modifier
            .wrapContentWidth()
            .padding(start = 5.dp)
            .clickable(onClick = {
                onLogout(true)
            }),
        text = stringResource(Res.string.everywhere),
        bold = true,
    )
}