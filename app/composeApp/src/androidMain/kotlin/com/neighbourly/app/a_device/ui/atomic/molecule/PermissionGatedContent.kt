package com.neighbourly.app.a_device.ui.atomic.molecule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.need_to_grant_permission
import neighbourly.composeapp.generated.resources.review_permissions
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionGatedContent(permissions: List<String>, content: @Composable () -> Unit) {
    val permissionState = rememberMultiplePermissionsState(permissions)
    var requestPermissions by remember { mutableStateOf(false) }

    LaunchedEffect(requestPermissions) {
        if (requestPermissions) {
            if (permissionState.permissions.any { !it.status.isGranted }) {
                permissionState.launchMultiplePermissionRequest()
            }
            requestPermissions = false
        }
    }

    if (permissionState.permissions.any { !it.status.isGranted }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FriendlyText(text = stringResource(Res.string.need_to_grant_permission))
            FriendlyButton(text = stringResource(Res.string.review_permissions)) {
                requestPermissions = true
            }
        }
    } else {
        content()
    }
}