package com.neighbourly.app.a_device.ui.atomic.organism.box

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.box.BoxListItem
import com.neighbourly.app.a_device.ui.atomic.molecule.misc.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.bean.BoxShareVS
import com.neighbourly.app.b_adapt.viewmodel.bean.BoxVS
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_box
import neighbourly.composeapp.generated.resources.deleteing_box
import neighbourly.composeapp.generated.resources.no_boxes
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismBoxList(
    boxes: List<BoxVS>,
    online: Boolean,
    removeBox: (id: String) -> Unit,
    editBox: (id: String, name: String) -> Unit,
    openBox: (id: String) -> Unit,
    unlockBox: (id: String, unlock: Boolean) -> Unit,
    lightBox: (id: String, light: Boolean) -> Unit,
    shareBox: (id: String) -> Unit,
    shareBoxSelect: (shareBox: BoxShareVS) -> Unit,
    shareBoxDelete: (shareBox: BoxShareVS) -> Unit,
) {
    var showRemoveAlertForId by remember { mutableStateOf("") }

    if (showRemoveAlertForId.isNotBlank()) {
        OrganismAlertDialog(
            title = stringResource(Res.string.deleteing_box),
            text = stringResource(Res.string.confirm_deleteing_box),
            ok = {
                removeBox(showRemoveAlertForId)
                showRemoveAlertForId = ""
            },
            cancel = {
                showRemoveAlertForId = ""
            }
        )
    }

    if (boxes.isEmpty()) {
        FriendlyText(text = stringResource(Res.string.no_boxes))
    } else {
        boxes.forEach { box ->
            SwipeToDeleteContainer(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                onDelete = {
                    showRemoveAlertForId = box.id
                }
            ) {
                BoxListItem(
                    box = box,
                    online = online,
                    editBox = editBox,
                    openBox = openBox,
                    unlockBox = unlockBox,
                    lightBox = lightBox,
                    shareBox = shareBox,
                    shareBoxSelect = shareBoxSelect,
                    shareBoxDelete = shareBoxDelete,
                )
            }
        }
    }
}