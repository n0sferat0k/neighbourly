package com.neighbourly.app.a_device.ui.web

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.neighbourly.app.PlatformWebView


@Composable
fun WebPageView(
    modifier: Modifier = Modifier,
    url: String,
) {
    val webViewState =
        rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()

    PlatformWebView(
        modifier = modifier,
        state = webViewState,
        navigator = navigator,
    )
}