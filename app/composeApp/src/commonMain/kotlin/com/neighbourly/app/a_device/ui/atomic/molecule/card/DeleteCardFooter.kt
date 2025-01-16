package com.neighbourly.app.a_device.ui.atomic.molecule.card

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_this_item
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.deleteing_item
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.register
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeleteCardFooter(
    onDelete: () -> Unit
) {
    var showDeleteAlert by remember { mutableStateOf(false) }
    if (showDeleteAlert) {
        OrganismAlertDialog(
            title = stringResource(Res.string.deleteing_item),
            text = stringResource(Res.string.confirm_deleteing_this_item),
            ok = {
                showDeleteAlert = false
                onDelete()
            },
            cancel = {
                showDeleteAlert = false
            }
        )
    }

    FriendlyText(
        modifier = Modifier.clickable {
            showDeleteAlert = true
        },
        text = stringResource(Res.string.delete), bold = true
    )
}