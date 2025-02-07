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
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismContentBubble
import com.neighbourly.app.b_adapt.viewmodel.box.BoxManagementViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_box
import org.jetbrains.compose.resources.stringResource

@Composable
fun BoxManagementTemplate(
    state: BoxManagementViewModel.BoxManagementViewState,
    addBox: (id: String) -> Unit,
    removeBox: (id: String) -> Unit,
    refresh: () -> Unit,
    updateName: (String) -> Unit,
    saveBox: () -> Unit,
    clearBox: () -> Unit,
    openBox: (id: String) -> Unit,
    unlockBox: (id: String, unlock: Boolean) -> Unit,
    lightBox: (id: String, light: Boolean) -> Unit,
) {
    var showBoxScanner by remember { mutableStateOf(false) }

    OrganismContentBubble(
        scrollable = true,
        busy = state.loading,
        refresh = refresh,
        content = {
            if (showBoxScanner) {
                OrganismBoxScanner { id ->
                    id?.let { addBox(it) }
                    showBoxScanner = false
                }
            } else if (state.newBoxId.isNotEmpty()) {
                OrganismBoxEditor(
                    id = state.newBoxId,
                    name = state.newBoxName,
                    nameError = state.newBoxNameError,
                    saving = state.saving,
                    updateName = updateName,
                    saveBox = saveBox,
                    clearBox = clearBox
                )
            } else {
                OrganismBoxList(
                    state.boxes,
                    removeBox = removeBox,
                    openBox = openBox,
                    unlockBox = unlockBox,
                    lightBox = lightBox
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