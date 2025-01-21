package com.neighbourly.app.a_device.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.neighbourly.app.PlatformBitmap


fun generateQrCode(content: String, size: Int): ImageBitmap {
    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, null)

    val bufferedImage = PlatformBitmap(size, size)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bufferedImage.setPixel(
                x, y,
                if (bitMatrix[x, y])
                    Color.Black.toArgb()
                else
                    Color.White.toArgb(),
            )
        }
    }
    return bufferedImage.asImageBitmap()
}


