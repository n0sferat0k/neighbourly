package com.neighbourly.app.a_device.ui.atomic.organism.box

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.neighbourly.app.a_device.ui.atomic.molecule.misc.SwipeToDeleteContainer
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.bean.BoxVS
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirm_deleteing_box
import neighbourly.composeapp.generated.resources.deleteing_box
import neighbourly.composeapp.generated.resources.light
import neighbourly.composeapp.generated.resources.list_shares
import neighbourly.composeapp.generated.resources.no_boxes
import neighbourly.composeapp.generated.resources.openbox
import neighbourly.composeapp.generated.resources.share
import neighbourly.composeapp.generated.resources.signal
import neighbourly.composeapp.generated.resources.unlockbox
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrganismBoxList(
    boxes: List<BoxVS>,
    editBox: (id: String, name: String) -> Unit,
    removeBox: (id: String) -> Unit,
    openBox: (id: String) -> Unit,
    unlockBox: (id: String, unlock: Boolean) -> Unit,
    lightBox: (id: String, light: Boolean) -> Unit,
    shareBox: (id: String) -> Unit,
    shareBoxSelect: (id: Int, boxId: String) -> Unit,
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
                }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier.height(48.dp)
                                    .clickable { editBox(box.id, box.name) },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                FriendlyText(
                                    text = box.name,
                                    bold = true,
                                    fontSize = 22.sp
                                )
                            }

                            Image(
                                painter = painterResource(Res.drawable.signal),
                                contentDescription = "Box online",
                                contentScale = ContentScale.FillBounds,
                                colorFilter = ColorFilter.tint(
                                    when (box.online) {
                                        null -> AppColors.primaryLight
                                        true -> AppColors.primary
                                        false -> AppColors.complementary
                                    }
                                ),
                                modifier = Modifier.size(48.dp).padding(6.dp),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Image(
                                painter = painterResource(Res.drawable.openbox),
                                contentDescription = "Open box",
                                contentScale = ContentScale.FillBounds,
                                colorFilter = ColorFilter.tint(if (box.triggered) AppColors.highlight else AppColors.primary),
                                modifier = Modifier.size(48.dp).clickable {
                                    openBox(box.id)
                                },
                            )
                            Image(
                                painter = painterResource(Res.drawable.unlockbox),
                                contentDescription = "Unlock box",
                                contentScale = ContentScale.FillBounds,
                                colorFilter = ColorFilter.tint(if (box.unlocked) AppColors.highlight else AppColors.primary),
                                modifier = Modifier.size(48.dp).clickable {
                                    unlockBox(box.id, !box.unlocked)
                                },
                            )
                            Image(
                                painter = painterResource(Res.drawable.light),
                                contentDescription = "Illuminate box",
                                contentScale = ContentScale.FillBounds,
                                colorFilter = ColorFilter.tint(if (box.lit) AppColors.highlight else AppColors.primary),
                                modifier = Modifier.size(48.dp).clickable {
                                    lightBox(box.id, !box.lit)
                                },
                            )

                            if(box.owned) {
                                Spacer(modifier = Modifier.weight(1f))

                                Image(
                                    painter = painterResource(Res.drawable.share),
                                    contentDescription = "Share box",
                                    contentScale = ContentScale.FillBounds,
                                    colorFilter = ColorFilter.tint(AppColors.primary),
                                    modifier = Modifier.size(48.dp).clickable {
                                        shareBox(box.id)
                                    },
                                )
                            }
                        }
                        if (box.shares.isNotEmpty() && box.owned) {
                            FriendlyText(text = stringResource(Res.string.list_shares))

                            box.shares.forEach {
                                FriendlyText(modifier = Modifier
                                    .padding(top = 4.dp, bottom = 4.dp)
                                    .clickable {
                                        shareBoxSelect(it.id, it.boxId)
                                    }, text = "* " + it.name, bold = true, fontSize = 22.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}