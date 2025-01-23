package com.neighbourly.app.a_device.ui.atomic.organism.box

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.PlatformBarcodeScanner
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismBoxScanner(onResult: (id: String?) -> Unit) {
    PlatformBarcodeScanner(modifier = Modifier.fillMaxSize()) { scanString ->
        onResult(scanString)
    }
    Box(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        FriendlyButton(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(Res.string.cancel),
        ) {
            onResult(null)
        }
    }
}