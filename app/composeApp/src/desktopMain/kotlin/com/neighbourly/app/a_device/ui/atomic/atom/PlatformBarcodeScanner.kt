package com.neighbourly.app.a_device.ui.atomic.atom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.github.eduramiba.webcamcapture.drivers.NativeDriver
import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamException
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skiko.toBitmap
import java.awt.image.BufferedImage

@Composable
actual fun PlatformBarcodeScanner(
    modifier: Modifier,
    onDone: ((String) -> Unit),
) {
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    var imageRatio by remember { mutableStateOf(1f) }
    val coroutineScope = rememberCoroutineScope()
    var webcam: Webcam

    try {
        webcam = Webcam.getDefault()
    } catch (e: WebcamException) {
        Webcam.setDriver(NativeDriver())
        webcam = Webcam.getWebcams().first()
    }

    // Start capturing the webcam feed on composition
    DisposableEffect(Unit) {
        // Use a coroutine to update the frame in the Composable
        coroutineScope.launch(Dispatchers.IO) {
            webcam.open(false) // open in non-blocking mode
            while (webcam.isOpen) {
                val bufferedImage: BufferedImage = webcam.image
                image = bufferedImage.toBitmap().asComposeImageBitmap()
                imageRatio = image?.let { it.width.toFloat() / it.height.toFloat() } ?: 1f
                decodeBarcode(bufferedImage)?.let { onDone(it) }
            }
        }

        onDispose {
            webcam.close()
        }
    }

    Box(modifier = modifier) {
        image?.let {
            Image(
                contentScale = ContentScale.FillWidth,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(imageRatio),
                bitmap = it,
                contentDescription = "Webcam Feed",
            )
        }
    }
}

fun decodeBarcode(image: BufferedImage): String? {
    try {
        val luminanceSource = BufferedImageLuminanceSource(image)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))
        val result = MultiFormatReader().decode(binaryBitmap)
        return result.text
    } catch (e: NotFoundException) {
        // Handle no barcode found in the image
        return null
    }
}
