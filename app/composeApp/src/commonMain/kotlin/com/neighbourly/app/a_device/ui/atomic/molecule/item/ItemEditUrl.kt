package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.item_url
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemEditUrl(
    hidden: Boolean = false,
    url: String,
    isError: Boolean = false,
    onChange: (url: String) -> Unit
) {
    AnimatedVisibility(!hidden) {
        OutlinedTextField(
            value = url,
            onValueChange = {
                onChange(it)
            },
            isError = isError,
            label = { Text(stringResource(Res.string.item_url)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}