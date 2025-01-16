package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.d_entity.util.isValidUrl
import jdk.jfr.Description
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.item_description
import neighbourly.composeapp.generated.resources.item_name
import neighbourly.composeapp.generated.resources.item_url
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemEditUrl(
    hidden: Boolean = false,
    url: String,
    onChange: (url: String) -> Unit
) {
    AnimatedVisibility(!hidden) {
        OutlinedTextField(
            value = url,
            onValueChange = {
                onChange(it)
            },
            isError = urlOverride?.let { it.isBlank() || !it.isValidUrl() }
                ?: false,
            label = { Text(stringResource(Res.string.item_url)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}