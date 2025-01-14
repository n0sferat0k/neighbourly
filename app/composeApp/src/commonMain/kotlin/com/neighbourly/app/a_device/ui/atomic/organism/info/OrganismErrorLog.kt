package com.neighbourly.app.a_device.ui.atomic.organism.info

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.last_error
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismErrorLog(lastError: String?) {
    if (lastError != null) {
        FriendlyText(
            modifier = Modifier.padding(20.dp),
            text = stringResource(Res.string.last_error),
            fontSize = 22.sp,
        )

        FriendlyErrorText(lastError.orEmpty())
    }
}