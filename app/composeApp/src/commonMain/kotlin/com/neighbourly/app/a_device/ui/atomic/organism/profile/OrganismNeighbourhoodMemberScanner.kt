package com.neighbourly.app.a_device.ui.atomic.organism.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.BarcodeScanner

@Composable
fun OrganismNeighbourhoodMemberScanner(
    onScan: (id: Int, user: String) -> Unit,
) {
    BarcodeScanner(modifier = Modifier.fillMaxSize()) { scanString ->
        scanString.split(",").let { tokens ->
            runCatching {
                onScan(tokens[0].toInt(), tokens[1])
            }
        }
    }
}
