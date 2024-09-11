package com.neighbourly.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.Map
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

fun main() {
    KoinProvider.initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Neighbourly",
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier =
                            Modifier
                                .width(440.dp)
                                .fillMaxHeight()
                                .background(AppColors.primaryLight),
                    ) {
                        App(false)
                    }
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(AppColors.complementaryLight),
                    ) {
                        DesktopMap()
                    }
                }
            }
        }
    }
}

@Composable
fun DesktopMap() {
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
            Map(modifier = Modifier.fillMaxSize())
        } else {
            Text(text = "Downloading $downloading%")
        }
    }
}
