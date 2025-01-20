@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.neighbourly.app

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.multiplatform.webview.jsbridge.WebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.WebViewState
import com.neighbourly.app.a_device.store.StatusMemoryStore
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.d_entity.interf.KeyValueRegistry
import dev.datlag.kcef.KCEF
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import java.awt.Image.SCALE_SMOOTH
import java.awt.image.BufferedImage
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO
import kotlin.io.path.readBytes
import kotlin.math.ceil
import kotlin.math.max

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

actual fun loadImageFromFile(file: String, maxSizePx: Int): BitmapPainter? {
    val originalImg = ImageIO.read(File(file)) ?: return null
    val maxDim = max(originalImg.width, originalImg.height)
    if (maxDim > maxSizePx) {
        val ratio = ceil(maxDim.toDouble() / maxSizePx.toDouble()).toInt()
        val newWidth = originalImg.width / ratio
        val newHeight = originalImg.height / ratio
        val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
        val graphics = resizedImage.createGraphics()
        graphics.drawImage(
            originalImg.getScaledInstance(newWidth, newHeight, SCALE_SMOOTH), 0, 0, null
        )
        graphics.dispose()
        return BitmapPainter(resizedImage.toComposeImageBitmap())
    } else {
        return BitmapPainter(originalImg.toComposeImageBitmap())
    }
}

actual fun loadImageFromFile(file: String): BitmapPainter? =
    ImageIO.read(File(file))?.let { BitmapPainter(it.toComposeImageBitmap()) }

actual fun loadNameFromFile(file: String): String = Paths.get(file).fileName.toString()

actual fun loadContentsFromFile(file: String): FileContents? {
    val path: Path = Paths.get(file)

    return FileContents(
        content = path.readBytes(),
        type = Files.probeContentType(path),
        name = path.fileName.toString(),
    )
}

actual fun getPhoneNumber(): String? = null

actual val httpClientEngine: HttpClientEngine = CIO.create()

actual val keyValueRegistry: KeyValueRegistry = DesktopFileBasedRegistry()

actual class PlatformBitmap actual constructor(width: Int, height: Int) {
    private val innerBmp: BufferedImage

    init {
        innerBmp = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    }

    actual fun setPixel(x: Int, y: Int, value: Int) =
        innerBmp.setRGB(x, y, value)

    actual fun asImageBitmap(): ImageBitmap = innerBmp.toComposeImageBitmap()
}

actual val databaseDriver: SqlDriver
    get() = JdbcSqliteDriver("jdbc:sqlite:neighbourly.db").also { NeighbourlyDB.Schema.create(it) }

actual val statusConfigSource = object : StatusMemoryStore() {
    override val wideScreenFlow: Flow<Boolean>
        get() = listOf(true).asFlow()
}

actual fun postSystemNotification(id: Int, title: String, text: String) {
}

actual val appVersionString: String
    get() = System.getProperty("app.version") ?: "Unknown"

@Composable
actual fun PlatformWebView(
    state: WebViewState,
    modifier: Modifier,
    navigator: WebViewNavigator,
    webViewJsBridge: WebViewJsBridge?
) {
    var restartRequired by remember { mutableStateOf(false) }
    var downloading by remember { mutableStateOf(0F) }
    var initialized by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            KCEF.disposeBlocking()
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            KCEF.init(
                builder = {
                    installDir(File("kcef-bundle"))
                    progress {
                        onDownloading { downloading = max(it, 0F) }
                        onInitialized { initialized = true }
                    }
                    settings { cachePath = File("cache").absolutePath }
                },
                onError = { it?.printStackTrace() },
                onRestartRequired = { restartRequired = true },
            )
        }
    }

    if (restartRequired) {
        Text(text = "Restart required.")
    } else {
        if (initialized) {
            WebView(
                state = state,
                modifier = modifier,
                navigator = navigator,
                webViewJsBridge = webViewJsBridge,
            )
        } else {
            Box(modifier = modifier) {
                Text(text = "Downloading $downloading%")
            }
        }
    }
}