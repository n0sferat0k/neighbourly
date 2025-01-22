package com.neighbourly.app.a_device.ui.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.neighbourly.app.PlatformWebView


@Composable
fun WebPageView(
    url: String,
) {
    val webViewState =
        rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()

    PlatformWebView(
        modifier = Modifier.fillMaxSize(),
        state = webViewState,
        navigator = navigator,
    )
}