package com.neighbourly.app.a_device.ui.atomic.organism.box

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.molecule.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_box
import neighbourly.composeapp.generated.resources.deleteing_box
import neighbourly.composeapp.generated.resources.no_boxes
import neighbourly.composeapp.generated.resources.openbox
import neighbourly.composeapp.generated.resources.unlockbox
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrganismBoxList(
    boxes: Map<String, String>?,
    removeBox: (id: String) -> Unit,
    openBox: (id: String) -> Unit,
    unlockBox: (id: String) -> Unit,
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

    if (boxes.isNullOrEmpty()) {
        FriendlyText(text = stringResource(Res.string.no_boxes))
    } else {
        boxes?.entries?.forEach { (id, name) ->
            SwipeToDeleteContainer(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                onDelete = {
                    showRemoveAlertForId = id
                }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 4.dp
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier.height(48.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            FriendlyText(
                                text = name,
                                bold = true,
                                fontSize = 22.sp
                            )
                        }

                        Image(
                            painter = painterResource(Res.drawable.openbox),
                            contentDescription = "Open box",
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(AppColors.primary),
                            modifier = Modifier.size(48.dp).clickable {
                                openBox(id)
                            },
                        )
                        Image(
                            painter = painterResource(Res.drawable.unlockbox),
                            contentDescription = "Unlock box",
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(AppColors.primary),
                            modifier = Modifier.size(48.dp).clickable {
                                unlockBox(id)
                            },
                        )
                    }
                }
            }
        }
    }
}