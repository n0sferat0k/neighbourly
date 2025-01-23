package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PlatformBarcodeScanner(
    modifier: Modifier = Modifier,
    onDone: ((String) -> Unit),
)