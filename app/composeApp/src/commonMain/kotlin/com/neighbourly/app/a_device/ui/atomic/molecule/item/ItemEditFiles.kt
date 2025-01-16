package com.neighbourly.app.a_device.ui.atomic.molecule.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.bean.AttachmentVS
import com.neighbourly.app.loadNameFromFile
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_file
import neighbourly.composeapp.generated.resources.confirm_deleteing_file
import neighbourly.composeapp.generated.resources.confirm_new_file
import neighbourly.composeapp.generated.resources.delete
import neighbourly.composeapp.generated.resources.deleteing_file
import neighbourly.composeapp.generated.resources.files
import neighbourly.composeapp.generated.resources.new_file
import neighbourly.composeapp.generated.resources.newbadge
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ItemEditFiles(
    hidden: Boolean = false,
    files: List<AttachmentVS>,
    newFiles: Map<String, String>,
    onUrlSelected: (url: String) -> Unit,
    onDeleteFile: (fileId: Int) -> Unit,
    onNewFiles: (newFiles: Map<String, String>) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val badge = painterResource(Res.drawable.newbadge)
    var showAttachmentFilePicker by remember { mutableStateOf(false) }
    var showRemoveAlertForFileId by remember { mutableStateOf(-1) }
    var showNewFileAlert by remember { mutableStateOf(false) }

    FilePicker(
        show = showAttachmentFilePicker,
        fileExtensions = listOf(
            "jpg",
            "png",
            "pdf",
            "txt",
            "svg",
            "doc",
            "docx",
            "ppt",
            "pptx",
            "*"
        )
    ) { file ->
        showAttachmentFilePicker = false
        file?.platformFile?.toString()?.let {
            onNewFiles(newFiles + (it to loadNameFromFile(it)))
        }
    }

    if (showRemoveAlertForFileId != -1) {
        OrganismAlertDialog(
            title = stringResource(Res.string.deleteing_file),
            text = stringResource(Res.string.confirm_deleteing_file),
            ok = {
                onDeleteFile(showRemoveAlertForFileId)
                showRemoveAlertForFileId = -1
            },
            cancel = {
                showRemoveAlertForFileId = -1
            }
        )
    }

    if (showNewFileAlert) {
        OrganismAlertDialog(
            title = stringResource(Res.string.new_file),
            text = stringResource(Res.string.confirm_new_file),
            ok = {
                showNewFileAlert = false
            },
        )
    }

    AnimatedVisibility(!hidden) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FriendlyText(text = stringResource(Res.string.files))
            FriendlyText(modifier = Modifier.clickable {
                focusManager.clearFocus(true)
                showAttachmentFilePicker = true
            }, text = stringResource(Res.string.add_file), bold = true)
        }

        files.onEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FriendlyText(
                    modifier = Modifier.weight(1f).padding(end = 5.dp).clickable {
                        onUrlSelected(it.url)
                    }, text = it.name, bold = true
                )

                FriendlyText(
                    modifier = Modifier.wrapContentWidth().clickable {
                        showRemoveAlertForFileId = it.id
                    },
                    text = stringResource(Res.string.delete),
                    bold = true
                )
            }
        }

        newFiles.onEach { newFile ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FriendlyText(
                    modifier = Modifier
                        .weight(1f).padding(end = 5.dp)
                        .clickable {
                            showNewFileAlert = true
                        }, text = newFile.value, bold = true
                )
                Image(
                    modifier = Modifier.size(36.dp),
                    painter = badge,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Item File New Badge",
                    colorFilter = ColorFilter.tint(AppColors.primary),
                )
                FriendlyText(
                    modifier = Modifier.wrapContentWidth().clickable {
                        onNewFiles(newFiles.filter { it != newFile })
                    },
                    text = stringResource(Res.string.delete),
                    bold = true
                )
            }
        }
    }
}