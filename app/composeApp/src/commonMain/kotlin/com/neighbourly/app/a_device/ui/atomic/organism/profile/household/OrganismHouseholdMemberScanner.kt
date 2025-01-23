package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.atom.PlatformBarcodeScanner

@Composable
fun OrganismHouseholdMemberScanner(
    onScan: (id: Int, user: String) -> Unit
) {
    PlatformBarcodeScanner(modifier = Modifier.fillMaxSize()) { scanString ->
        scanString.split(",").let { tokens ->
            runCatching {
                onScan(tokens[0].toInt(), tokens[1])
            }
        }
    }
}
