package com.neighbourly.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebViewState

interface Platform {
    val isWide: Boolean
}

expect fun getPlatform(): Platform
