package com.neighbourly.app.a_device.ui.atomic.organism.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.organism.menu.MenuTabVS
import com.neighbourly.app.a_device.ui.web.WebContentView
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent

@Composable
fun OrganismHostContentOverWeb(
    showLanding: Boolean,
    showAuth: Boolean,
    showContent: Boolean,
    mainContent: MainContent,
    isOnline: Boolean,
    onCrownClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: (tab: MenuTabVS) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        WebContentView(
            modifier = Modifier.fillMaxSize(),
        )
        OrganismHostContent(
            showLanding = showLanding,
            showAuth = showAuth,
            showContent = showContent,
            mainContent = mainContent,
            isOnline = isOnline,
            onCrownClick = onCrownClick,
            onHomeClick = onHomeClick,
            onMenuClick = onMenuClick,
        )
    }
}
