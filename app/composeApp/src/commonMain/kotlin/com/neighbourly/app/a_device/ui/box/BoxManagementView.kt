package com.neighbourly.app.a_device.ui.box

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.BarcodeScanner
import com.neighbourly.app.a_device.ui.utils.AppColors
import com.neighbourly.app.a_device.ui.utils.BoxFooter
import com.neighbourly.app.a_device.ui.utils.BoxHeader
import com.neighbourly.app.a_device.ui.utils.BoxScrollableContent
import com.neighbourly.app.a_device.ui.utils.CurlyButton
import com.neighbourly.app.a_device.ui.utils.FriendlyErrorText
import com.neighbourly.app.a_device.ui.utils.FriendlyText
import com.neighbourly.app.b_adapt.viewmodel.box.BoxManagementViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.add_box
import neighbourly.composeapp.generated.resources.box_id
import neighbourly.composeapp.generated.resources.box_name
import neighbourly.composeapp.generated.resources.cancel
import neighbourly.composeapp.generated.resources.no_boxes
import neighbourly.composeapp.generated.resources.openbox
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.unlockbox
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BoxManagementView(viewModel: BoxManagementViewModel = viewModel { KoinProvider.KOIN.get<BoxManagementViewModel>() }) {
    val state by viewModel.state.collectAsState()

    var showBoxScanner by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start), busy = state.loading) {
            viewModel.refresh()
        }

        BoxScrollableContent(modifier = Modifier.weight(1f)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showBoxScanner) {
                    BarcodeScanner(modifier = Modifier.fillMaxSize()) { scanString ->
                        viewModel.addBox(scanString)
                        showBoxScanner = false
                    }
                    CurlyButton(
                        text = stringResource(Res.string.cancel),
                    ) {
                        showBoxScanner = false
                    }
                } else if (state.newBoxId.isNotEmpty()) {
                    OutlinedTextField(
                        value = state.newBoxName,
                        onValueChange = {
                            viewModel.updateName(it)
                        },
                        label = { Text(stringResource(Res.string.box_name)) },
                        isError = state.newBoxNameError,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.newBoxId,
                        enabled = false,
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.box_id)) },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CurlyButton(
                            text = stringResource(Res.string.save),
                            loading = state.saving,
                        ) {
                            viewModel.saveBox()
                        }
                        CurlyButton(
                            text = stringResource(Res.string.cancel),
                        ) {
                            viewModel.clearBox()
                        }
                    }
                } else {
                    if (state.boxes.isNullOrEmpty()) {
                        FriendlyText(text = stringResource(Res.string.no_boxes))
                    } else {
                        state.boxes?.entries?.forEach { (id, name) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FriendlyText(text = name, bold = true, fontSize = 22.sp)
                                Image(
                                    painter = painterResource(Res.drawable.openbox),
                                    contentDescription = "Open box",
                                    contentScale = ContentScale.FillBounds,
                                    colorFilter = ColorFilter.tint(AppColors.primary),
                                    modifier = Modifier.size(48.dp).clickable {
                                        viewModel.openBox(id)
                                    },
                                )
                                Image(
                                    painter = painterResource(Res.drawable.unlockbox),
                                    contentDescription = "Unlock box",
                                    contentScale = ContentScale.FillBounds,
                                    colorFilter = ColorFilter.tint(AppColors.primary),
                                    modifier = Modifier.size(48.dp).clickable {
                                        viewModel.unlockBox(id)
                                    },
                                )
                            }
                        }
                    }
                }

                if (state.error.isNotEmpty()) {
                    FriendlyErrorText(state.error)
                }
            }
        }

        BoxFooter(modifier = Modifier.align(Alignment.End)) {
            FriendlyText(
                modifier = Modifier.clickable {
                    showBoxScanner = true
                },
                text = stringResource(Res.string.add_box), bold = true
            )
        }
    }
}