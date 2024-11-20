package com.neighbourly.app.a_device.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.utils.generateQrCode

@Composable
fun BackendInfoView() {
    Image(
        painter = BitmapPainter(
            generateQrCode(
                "http://neighbourly.go.ro/releases/neighbourly-1.0.0.apk",
                400
            )
        ),
        contentDescription = "QR Code",
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    )
}