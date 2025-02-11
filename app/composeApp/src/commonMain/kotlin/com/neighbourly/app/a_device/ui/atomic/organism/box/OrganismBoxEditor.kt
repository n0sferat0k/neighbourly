package com.neighbourly.app.a_device.ui.atomic.organism.box

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.box_id
import neighbourly.composeapp.generated.resources.box_name
import neighbourly.composeapp.generated.resources.cancel
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismBoxEditor(
    id: String,
    name: String,
    saving: Boolean,
    label: String = stringResource(Res.string.box_name),
    onSave: (name: String) -> Unit,
    clearBox: () -> Unit,
) {
    var nameOverride by remember { mutableStateOf<String?>(null) }

    OutlinedTextField(
        value = nameOverride ?: name,
        onValueChange = {
            nameOverride = it
        },
        label = { Text(label) },
        isError = (nameOverride ?: name).isBlank(),
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = id,
        enabled = false,
        onValueChange = {},
        label = { Text(stringResource(Res.string.box_id)) },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FriendlyButton(
            text = stringResource(Res.string.save),
            loading = saving,
        ) {
            onSave(nameOverride ?: name)
        }
        FriendlyButton(
            text = stringResource(Res.string.cancel),
        ) {
            clearBox()
        }
    }
}