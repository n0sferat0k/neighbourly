package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.item_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemEditDescription(
    hidden: Boolean = false,
    description: String,
    onChange: (description: String) -> Unit
) {
    AnimatedVisibility(!hidden) {
        OutlinedTextField(
            value = description,
            onValueChange = {
                onChange(it)
            },
            maxLines = 5,
            label = { Text(stringResource(Res.string.item_description)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}