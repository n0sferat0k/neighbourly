package com.neighbourly.app.a_device.ui.web

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState


@Composable
fun WebGalleryView(
    modifier: Modifier = Modifier,
    itemId: Int,
    imageId: Int,
) {
    val webViewState =
        rememberWebViewState(url = "http://neighbourly.go.ro/gallery.php?itemId=$itemId&imageId=$imageId")
    val navigator = rememberWebViewNavigator()

    WebView(
        modifier = modifier,
        state = webViewState,
        navigator = navigator,
    )
}