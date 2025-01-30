package com.neighbourly.app.a_device.ui.atomic.organism.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.CrownMenuItem
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.brain
import neighbourly.composeapp.generated.resources.signal
import org.jetbrains.compose.resources.painterResource

@Composable
fun OrganismCrownMenu(
    isOnline: Boolean = true,
    isAiOnline: Boolean = true,
    onWifiClick: () -> Unit,
    onAiClick: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        CrownMenuItem(isActive = isOnline, painter = painterResource(Res.drawable.signal)) {
            onWifiClick()
        }
        CrownMenuItem(isActive = isAiOnline, painter = painterResource(Res.drawable.brain)) {
            onAiClick()
        }
    }
}