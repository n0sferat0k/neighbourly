package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AlertDialog
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.CurlyButton
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.a_device.ui.utils.generateQrCode
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodInfoViewModel
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.addperson
import neighbourly.composeapp.generated.resources.cancel
import neighbourly.composeapp.generated.resources.cannot_create_neighbourhood
import neighbourly.composeapp.generated.resources.confirm_leaving_neighbourhood
import neighbourly.composeapp.generated.resources.create
import neighbourly.composeapp.generated.resources.create_neighbourhood
import neighbourly.composeapp.generated.resources.draw_neighbourhood
import neighbourly.composeapp.generated.resources.exit
import neighbourly.composeapp.generated.resources.join_neighbourhood
import neighbourly.composeapp.generated.resources.leaving
import neighbourly.composeapp.generated.resources.list_neighbourhoods
import neighbourly.composeapp.generated.resources.need_to_have_household
import neighbourly.composeapp.generated.resources.neighbourhoodName
import neighbourly.composeapp.generated.resources.no_neighbourhood
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NeighbourhoodInfoEditView(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: NeighbourhoodInfoViewModel = viewModel { KoinProvider.KOIN.get<NeighbourhoodInfoViewModel>() },
) {
    val state by viewModel.state.collectAsState()
    var showRemoveAlertForId by remember { mutableStateOf(-1) }

    if (state.drawing) {
        CurlyText(text = stringResource(Res.string.draw_neighbourhood))

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.nameOverride ?: state.name,
            enabled = state.drawingDone,
            onValueChange = {
                viewModel.updateName(it)
            },
            label = { Text(stringResource(Res.string.neighbourhoodName)) },
            isError = state.nameError,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
            CurlyButton(text = stringResource(Res.string.save), loading = state.saving) {
                viewModel.onSaveNeighbourhood()
            }

            CurlyButton(text = stringResource(Res.string.cancel), loading = state.saving) {
                viewModel.onCancelNeighbourhoodCreate()
            }
        }
    } else if (!state.hasLocalizedHouse) {
        CurlyText(text = stringResource(Res.string.need_to_have_household))
    } else {
        if (state.hasNeighbourhoods) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))

                CurlyText(text = stringResource(Res.string.list_neighbourhoods))

                Spacer(modifier = Modifier.height(8.dp))

                state.neighbourhoods.toList().forEach { neighbourhood ->
                    CurlyText(
                        modifier = Modifier.align(Alignment.Start),
                        text = "${neighbourhood.name} [Acc: ${neighbourhood.acc}]",
                        fontSize = 24.sp,
                        bold = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(Res.drawable.addperson),
                            contentDescription = "Add to neighbourhood",
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(AppColors.primary),
                            modifier =
                            Modifier.size(48.dp).clickable {
                                navigationViewModel.goToScanMemberHouseholdForNeighbourhood(
                                    neighbourhood.id
                                )
                            },
                        )

                        if (state.isHouseholdHead) {
                            Spacer(modifier = Modifier.width(20.dp))

                            if (showRemoveAlertForId == neighbourhood.id) {
                                AlertDialog(
                                    title = stringResource(Res.string.leaving) + " " + neighbourhood.name,
                                    text = stringResource(Res.string.confirm_leaving_neighbourhood),
                                    ok = {
                                        showRemoveAlertForId = -1
                                        viewModel.leaveNeighbourhood(neighbourhood.id)
                                    },
                                    cancel = {
                                        showRemoveAlertForId = -1
                                    }
                                )
                            }

                            Image(
                                painter = painterResource(Res.drawable.exit),
                                contentDescription = "Leave neighbourhood",
                                contentScale = ContentScale.FillBounds,
                                colorFilter = ColorFilter.tint(AppColors.primary),
                                modifier =
                                Modifier.size(48.dp).clickable {
                                    showRemoveAlertForId = neighbourhood.id
                                },
                            )
                        }
                    }
                }
            }
        } else {
            CurlyText(text = stringResource(Res.string.no_neighbourhood))
        }

        Spacer(modifier = Modifier.height(8.dp))

        CurlyText(text = stringResource(Res.string.join_neighbourhood))

        state.userQr.takeIf { !it.isNullOrBlank() }?.let {
            Image(
                painter = BitmapPainter(generateQrCode(it, 400)),
                contentDescription = "QR Code",
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        }
        if (state.isHouseholdHead) {
            CurlyText(text = stringResource(Res.string.create_neighbourhood))

            Spacer(modifier = Modifier.height(8.dp))

            CurlyButton(text = stringResource(Res.string.create)) {
                viewModel.createNeighbourhood()
                navigationViewModel.goToMap()
            }
        } else {
            CurlyText(text = stringResource(Res.string.cannot_create_neighbourhood))
        }
    }
}
