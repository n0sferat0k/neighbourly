package com.neighbourly.app.a_device.ui.atomic.organism.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.dialog.AlertDialogContent
import com.neighbourly.app.a_device.ui.atomic.molecule.dialog.AlertDialogFlowRow
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.cancel
import neighbourly.composeapp.generated.resources.confirm
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismAlertDialog(
    title: String,
    text: String,
    ok: (() -> Unit)? = null,
    cancel: (() -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = { cancel?.invoke() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AlertDialogContent(
                modifier = Modifier.align(Alignment.CenterStart).widthIn(max = 440.dp)
                    .padding(20.dp),
                buttons = {
                    Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp)) {
                        AlertDialogFlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 12.dp
                        ) {
                            if (cancel != null) {
                                FriendlyButton(
                                    text = stringResource(Res.string.cancel),
                                    modifier = Modifier.padding(5.dp)
                                ) {
                                    cancel.invoke()
                                }
                            }
                            FriendlyButton(
                                text = stringResource(Res.string.confirm),
                                modifier = Modifier.padding(5.dp)
                            ) {
                                ok?.invoke()
                            }
                        }
                    }
                },
                title = { FriendlyText(text = title) },
                text = { FriendlyText(text = text) },
            )
        }
    }
}