package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.CurlyButton
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.a_device.ui.ErrorText
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdInfoEditViewModel
import com.neighbourly.app.generateQrCode
import com.neighbourly.app.loadContentsFromFile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.about
import neighbourly.composeapp.generated.resources.add_member
import neighbourly.composeapp.generated.resources.address
import neighbourly.composeapp.generated.resources.create_from_scratch
import neighbourly.composeapp.generated.resources.householdName
import neighbourly.composeapp.generated.resources.houses
import neighbourly.composeapp.generated.resources.invite_or_create_household
import neighbourly.composeapp.generated.resources.list_household_members
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HouseholdInfoEditView(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: HouseholdInfoEditViewModel = viewModel { KoinProvider.KOIN.get<HouseholdInfoEditViewModel>() },
) {
    val state by viewModel.state.collectAsState()
    val navigation by navigationViewModel.state.collectAsState()

    val defaultHouseImg = painterResource(Res.drawable.houses)
    var showFilePicker by remember { mutableStateOf(false) }

    if (!state.hasHousehold && !navigation.addingNewHousehold) {
        Column {
            CurlyText(text = stringResource(Res.string.invite_or_create_household))

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

            CurlyText(
                modifier =
                    Modifier.clickable {
                        navigationViewModel.goToAddHousehold()
                    },
                bold = true,
                text = stringResource(Res.string.create_from_scratch),
            )
        }
    } else {
        Column {
            Row {
                // Name Input
                OutlinedTextField(
                    value = state.nameOverride ?: state.name,
                    enabled = state.editableHousehold || navigation.addingNewHousehold,
                    onValueChange = {
                        viewModel.updateName(it)
                    },
                    label = { Text(stringResource(Res.string.householdName)) },
                    isError = state.nameError,
                    modifier = Modifier.weight(1f),
                )

                if (state.hasHousehold) {
                    MultipleFilePicker(
                        show = showFilePicker,
                        fileExtensions = listOf("jpg", "png"),
                    ) { file ->
                        showFilePicker = false

                        file?.get(0)?.platformFile?.toString()?.let {
                            viewModel.onHouseholdImageUpdate(loadContentsFromFile(it))
                        }
                    }

                    Spacer(modifier = Modifier.width(3.dp))

                    Box(
                        modifier =
                            Modifier
                                .size(60.dp)
                                .align(Alignment.Bottom)
                                .border(2.dp, AppColors.primary, CircleShape)
                                .clickable { showFilePicker = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        state.imageurl.let {
                            if (!it.isNullOrBlank() && !state.imageUpdating) {
                                KamelImage(
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    resource = asyncPainterResource(data = it),
                                    contentDescription = "Profile Image",
                                    contentScale = ContentScale.Crop,
                                    onLoading = { progress ->
                                        CircularProgressIndicator(
                                            progress = progress,
                                            color = AppColors.primary,
                                        )
                                    },
                                )
                            } else if (state.imageUpdating) {
                                CircularProgressIndicator(color = AppColors.primary)
                            } else {
                                Image(
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    painter = defaultHouseImg,
                                    contentDescription = "Household Image",
                                    colorFilter = ColorFilter.tint(AppColors.primary),
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Address Input
            OutlinedTextField(
                value = state.addressOverride ?: state.address,
                enabled = state.editableHousehold || navigation.addingNewHousehold,
                onValueChange = {
                    viewModel.updateAddress(it)
                },
                label = { Text(stringResource(Res.string.address)) },
                isError = state.addressError,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // About Input
            OutlinedTextField(
                value = state.aboutOverride ?: state.about,
                enabled = state.editableHousehold || navigation.addingNewHousehold,
                onValueChange = {
                    viewModel.updateAbout(it)
                },
                maxLines = 5,
                label = { Text(stringResource(Res.string.about)) },
                modifier = Modifier.fillMaxWidth(),
            )

            if (state.editableHousehold || navigation.addingNewHousehold) {
                Spacer(modifier = Modifier.height(8.dp))

                CurlyButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(Res.string.save),
                    loading = state.saving,
                ) {
                    viewModel.onSaveHousehold()
                }

                if (state.error.isNotEmpty()) {
                    ErrorText(state.error)
                }

                Spacer(modifier = Modifier.height(8.dp))

                CurlyText(
                    modifier =
                        Modifier
                            .clickable {
                                navigationViewModel.goToAddMember()
                            }.align(Alignment.Start),
                    bold = true,
                    text = stringResource(Res.string.add_member),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.members != null) {
                CurlyText(text = stringResource(Res.string.list_household_members))

                state.members?.forEach {
                    CurlyText(text = "* " + it, bold = true)
                }
            }
        }
    }
}