package com.neighbourly.app.a_device.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BarcodeScanner(
    modifier: Modifier = Modifier,
    onDone: ((String) -> Unit),
)
