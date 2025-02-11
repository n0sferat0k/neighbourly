package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.box.OrganismBoxEditor
import com.neighbourly.app.a_device.ui.atomic.organism.box.OrganismBoxList
import com.neighbourly.app.a_device.ui.atomic.organism.box.OrganismBoxScanner
import com.neighbourly.app.a_device.ui.atomic.organism.box.OrganismBoxShare
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.box.BoxManagementViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_box
import neighbourly.composeapp.generated.resources.box_share_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun BoxManagementTemplate(
    state: BoxManagementViewModel.BoxManagementViewState,
    addBox: (scanResult: String) -> Unit,
    editBox: (id: String, name: String) -> Unit,
    removeBox: (id: String) -> Unit,
    refresh: () -> Unit,
    saveBox: (name: String) -> Unit,
    saveBoxShare: (name: String) -> Unit,
    clearBox: () -> Unit,
    openBox: (id: String) -> Unit,
    unlockBox: (id: String, unlock: Boolean) -> Unit,
    lightBox: (id: String, light: Boolean) -> Unit,
    shareBox: (id: String) -> Unit,
    shareBoxSelect: (id: Int, boxId: String) -> Unit,
    onHouseholdClick: (householdId: Int) -> Unit,
) {
    var showBoxScanner by remember { mutableStateOf(false) }

    OrganismContentBubble(
        scrollable = true,
        busy = state.loading,
        refresh = refresh,
        content = {
            when {
                showBoxScanner -> OrganismBoxScanner { scanResult ->
                    scanResult?.let { addBox(it) }
                    showBoxScanner = false
                }

                state.newBoxId.isNotEmpty() -> OrganismBoxEditor(
                    id = state.newBoxId,
                    name = state.boxName,
                    saving = state.saving,
                    onSave = saveBox,
                    clearBox = clearBox
                )

                state.shareableBoxId.isNotEmpty() -> OrganismBoxEditor(
                    id = state.shareableBoxId,
                    name = "",
                    saving = state.saving,
                    label = stringResource(Res.string.box_share_name),
                    onSave = saveBoxShare,
                    clearBox = clearBox
                )

                state.shareBox != null -> OrganismBoxShare(
                    shareBox = state.shareBox,
                    onHouseholdClick = onHouseholdClick
                )

                else -> OrganismBoxList(
                    state.boxes,
                    editBox = editBox,
                    removeBox = removeBox,
                    openBox = openBox,
                    unlockBox = unlockBox,
                    lightBox = lightBox,
                    shareBox = shareBox,
                    shareBoxSelect = shareBoxSelect,
                )
            }
        },
        footerContent = {

            FriendlyText(
                modifier = Modifier.clickable {
                    showBoxScanner = true
                },
                text = stringResource(Res.string.add_box), bold = true
            )
        }
    )
}