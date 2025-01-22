package com.neighbourly.app.a_device.ui.atomic.organism.profile.neighbourhood

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.cancel
import neighbourly.composeapp.generated.resources.draw_neighbourhood
import neighbourly.composeapp.generated.resources.neighbourhoodName
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismNeighbourhoodNew(
    hasGeofence: Boolean,
    saving: Boolean,
    onSave: (name: String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    FriendlyText(text = stringResource(Res.string.draw_neighbourhood))

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = name,
        enabled = hasGeofence,
        onValueChange = {
            name = it
        },
        label = { Text(stringResource(Res.string.neighbourhoodName)) },
        isError = name.isBlank(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
        FriendlyButton(text = stringResource(Res.string.save), loading = saving) {
            onSave(name)
        }

        FriendlyButton(text = stringResource(Res.string.cancel)) {
            onCancel()
        }
    }
}