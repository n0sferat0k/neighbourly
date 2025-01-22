package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.accept
import neighbourly.composeapp.generated.resources.localize_completed
import neighbourly.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismHouseholdLocalizeComplete(
    onAccept: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        FriendlyText(text = stringResource(Res.string.localize_completed))

        Row {
            FriendlyButton(text = stringResource(Res.string.accept)) {
                onAccept()
            }

            Spacer(modifier = Modifier.width(8.dp))

            FriendlyButton(text = stringResource(Res.string.retry)) {
                onRetry()
            }
        }
    }
}