package com.neighbourly.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.readBytes

class JVMPlatform : Platform {
    override val isWide: Boolean = true
}

actual fun getPlatform(): Platform = JVMPlatform()

@Composable
actual fun requestPermissions() {
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

actual fun loadImageFromFile(file: String): BitmapPainter? = BitmapPainter(ImageIO.read(File(file)).toComposeImageBitmap())

actual fun loadImageFromByteAray(content: ByteArray): BitmapPainter? =
    BitmapPainter(ImageIO.read(content.inputStream()).toComposeImageBitmap())

actual fun loadContentsFromFile(file: String): FileContents? {
    val path: Path = Paths.get(file)

    return FileContents(
        content = path.readBytes(),
        type = Files.probeContentType(path),
        name = path.fileName.toString(),
    )
}

actual fun getPhoneNumber(): String = ""

actual val httpClientEngine: HttpClientEngine = CIO.create()
actual val keyValueRegistry: KeyValueRegistry = DesktopFileBasedRegistry()
actual val isLargeLandscape: Boolean
    get() = TODO("Not yet implemented")
