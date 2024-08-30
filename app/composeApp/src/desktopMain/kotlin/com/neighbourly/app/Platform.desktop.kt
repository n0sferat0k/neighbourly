package com.neighbourly.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.darkrockstudios.libraries.mpfilepicker.MPFile
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

@Composable
actual fun RequestPermissions() {
}


actual object GetLocation {
    actual fun addCallback(callback: GeoLocationCallback) {
        val urlConnection =
            URL("http://ip-api.com/csv/?fields=lat,lon").openConnection() as HttpURLConnection
        try {
            urlConnection.requestMethod = "GET"
            urlConnection.inputStream.bufferedReader().use {
                it.readText().split(",").let {
                    callback(it[0].toDouble(), it[1].toDouble(), Float.MAX_VALUE)
                }
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    actual fun removeCallback(callback: GeoLocationCallback) {

    }
}

actual fun loadImageFromFile(file: MPFile<Any>): BitmapPainter {
    val imageFile = File(file.platformFile.toString())
    return BitmapPainter(ImageIO.read(imageFile).toComposeImageBitmap())
}