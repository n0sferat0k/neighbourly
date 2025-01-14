package com.neighbourly.app.a_device.ui.atomic.organism.info

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.app_debug
import neighbourly.composeapp.generated.resources.app_version
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismAppInfo(appVersion: String, isDebug: Boolean) {
    FriendlyText(
        text = stringResource(Res.string.app_version, appVersion),
        fontSize = 22.sp,
    )
    FriendlyText(
        text = stringResource(Res.string.app_debug, isDebug),
        fontSize = 22.sp,
    )
}