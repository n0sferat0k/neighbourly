package com.neighbourly.app.a_device.ui.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.neighbourly.app.PlatformWebView


@Composable
fun WebGalleryView(
    itemId: Int,
    imageId: Int,
) {
    val webViewState =
        rememberWebViewState(url = "http://neighbourly.go.ro/gallery.php?itemId=$itemId&imageId=$imageId")
    val navigator = rememberWebViewNavigator()

    PlatformWebView(
        modifier = Modifier.fillMaxSize(),
        state = webViewState,
        navigator = navigator,
    )
}