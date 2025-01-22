package com.neighbourly.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.neighbourly.app.a_device.ui.atomic.page.HostPage

fun main() {
    KoinProvider.initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Neighbourly",
        ) {
            HostPage()
        }
    }
}

