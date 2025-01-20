package com.neighbourly.app.a_device.ui.atomic.organism.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.MainContent
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.CrownMenuItem
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.HomeMenuItem

@Composable
fun OrganismHostContent(
    showMain: Boolean,
    isOnline: Boolean,
    onCrownClick: () -> Unit,
    onHomeClick: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        CrownMenuItem(isOnline = isOnline) {
            onCrownClick()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        HomeMenuItem(onHomeClick)

        if (showMain) {
            MainContent()
        }
    }
}
