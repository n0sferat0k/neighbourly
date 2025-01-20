package com.neighbourly.app.a_device.ui.atomic.organism.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.web.WebContentView

@Composable
fun OrganismHostContentOverWeb(
    showMain: Boolean,
    isOnline: Boolean,
    onCrownClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        WebContentView(
            modifier = Modifier.fillMaxSize(),
        )
        OrganismHostContent(
            showMain = showMain,
            isOnline = isOnline,
            onCrownClick = onCrownClick,
            onHomeClick = onHomeClick
        )
    }
}
