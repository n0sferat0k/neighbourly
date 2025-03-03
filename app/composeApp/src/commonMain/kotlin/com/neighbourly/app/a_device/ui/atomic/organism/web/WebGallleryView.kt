package com.neighbourly.app.a_device.ui.atomic.organism.web

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.neighbourly.app.a_device.ui.atomic.atom.PlatformWebView


@Composable
fun WebGalleryView(
    householdId: Int? = null,
    itemId: Int? = null,
    imageId: Int,
) {
    val webViewState =
        rememberWebViewState(url = "https://neighbourlybox.com/gallery.php?${if(itemId != null) "itemId=$itemId" else "householdId=$householdId" }&imageId=$imageId")
    val navigator = rememberWebViewNavigator()

    PlatformWebView(
        modifier = Modifier.fillMaxSize(),
        state = webViewState,
        navigator = navigator,
    )
}