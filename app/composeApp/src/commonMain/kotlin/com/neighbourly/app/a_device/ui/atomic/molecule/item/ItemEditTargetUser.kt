package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.molecule.misc.AutocompleteOutlinedTextField
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.target_user
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemEditTargetUser(
    hidden: Boolean = false,
    selectedUserId: Int?,
    users: Map<Int, String>,
    onSelectTargetUser: (id: Int) -> Unit
) {
    AnimatedVisibility(!hidden) {
        AutocompleteOutlinedTextField(
            text = selectedUserId?.let { userId ->
                users.getOrDefault(userId, "")
            } ?: "",
            label = { Text(stringResource(Res.string.target_user)) },
            entries = users,
            modifier = Modifier.fillMaxWidth(),
            onSelect = onSelectTargetUser
        )
    }
}