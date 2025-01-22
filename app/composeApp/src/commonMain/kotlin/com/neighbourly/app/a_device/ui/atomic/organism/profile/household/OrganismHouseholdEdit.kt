package com.neighbourly.app.a_device.ui.atomic.organism.profile.household

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismAlertDialog
import com.neighbourly.app.b_adapt.viewmodel.bean.HouseholdVS
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.LocalKamelConfig
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.about
import neighbourly.composeapp.generated.resources.add_member
import neighbourly.composeapp.generated.resources.address
import neighbourly.composeapp.generated.resources.confirm_leaving_household
import neighbourly.composeapp.generated.resources.householdName
import neighbourly.composeapp.generated.resources.houses
import neighbourly.composeapp.generated.resources.leave_household
import neighbourly.composeapp.generated.resources.leaving
import neighbourly.composeapp.generated.resources.list_household_members
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismHouseholdEdit(
    household: HouseholdVS?,
    editableHousehold: Boolean,
    saving: Boolean,
    imageUpdating: Boolean,
    onImageUpdate: (file: String) -> Unit,
    onSaveHousehold: (name: String?, address: String?, about: String?) -> Unit,
    onAddMember: () -> Unit,
    onLeaveHousehold: () -> Unit,
    members: List<String>?,
) {
    val defaultHouseImg = painterResource(Res.drawable.houses)

    var showFilePicker by remember { mutableStateOf(false) }
    var showRemoveAlert by remember { mutableStateOf(false) }

    var nameOverride by remember { mutableStateOf<String?>(null) }
    var addressOverride by remember { mutableStateOf<String?>(null) }
    var aboutOverride by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(household) {
        nameOverride = null
        addressOverride = null
        aboutOverride = null
    }

    val hasChanged by derivedStateOf {
        listOf(
            nameOverride,
            addressOverride,
            aboutOverride,
        ).any { it != null }
    }

    if (showRemoveAlert) {
        OrganismAlertDialog(
            title = stringResource(Res.string.leaving) + " " + household?.name.orEmpty(),
            text = stringResource(Res.string.confirm_leaving_household),
            ok = {
                showRemoveAlert = false
                onLeaveHousehold()
            },
            cancel = {
                showRemoveAlert = false
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row {
            // Name Input
            OutlinedTextField(
                value = nameOverride ?: household?.name ?: "",
                enabled = editableHousehold,
                onValueChange = {
                    nameOverride = it
                },
                label = { Text(stringResource(Res.string.householdName)) },
                isError = (nameOverride ?: household?.name)?.isBlank() ?: true,
                modifier = Modifier.weight(1f),
            )

            if (household != null) {
                FilePicker(
                    show = showFilePicker,
                    fileExtensions = listOf("jpg", "png"),
                ) { file ->
                    showFilePicker = false

                    file?.platformFile?.toString()?.let {
                        onImageUpdate(it)
                    }
                }

                Spacer(modifier = Modifier.width(3.dp))

                Box(
                    modifier =
                    Modifier
                        .size(60.dp)
                        .align(Alignment.Bottom)
                        .border(2.dp, AppColors.primary, CircleShape)
                        .clickable {
                            if (editableHousehold) {
                                showFilePicker = true
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    household.imageurl.let {
                        if (!it.isNullOrBlank() && !imageUpdating) {
                            LocalKamelConfig
                            KamelImage(
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                resource = asyncPainterResource(data = it),
                                contentDescription = "Household Image",
                                contentScale = ContentScale.Crop,
                                onLoading = { progress ->
                                    CircularProgressIndicator(
                                        progress = progress,
                                        color = AppColors.primary,
                                    )
                                },
                            )
                        } else if (imageUpdating) {
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

        // Address Input
        OutlinedTextField(
            value = addressOverride ?: household?.address ?: "",
            enabled = editableHousehold,
            onValueChange = {
                addressOverride = it
            },
            label = { Text(stringResource(Res.string.address)) },
            isError = (addressOverride ?: household?.address)?.isBlank() ?: true,
            modifier = Modifier.fillMaxWidth(),
        )

        // About Input
        OutlinedTextField(
            value = aboutOverride ?: household?.about ?: "",
            enabled = editableHousehold,
            onValueChange = {
                aboutOverride = it
            },
            maxLines = 5,
            label = { Text(stringResource(Res.string.about)) },
            modifier = Modifier.fillMaxWidth(),
        )

        if (hasChanged && editableHousehold) {
            FriendlyButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(Res.string.save),
                loading = saving,
            ) {
                onSaveHousehold(nameOverride, addressOverride, aboutOverride)
            }
        }

        if (household != null) {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (editableHousehold) {
                    FriendlyText(
                        modifier =
                        Modifier
                            .clickable {
                                onAddMember()
                            }.align(Alignment.CenterStart),
                        bold = true,
                        text = stringResource(Res.string.add_member),
                    )
                }

                FriendlyText(
                    modifier =
                    Modifier
                        .clickable {
                            showRemoveAlert = true
                        }.align(Alignment.CenterEnd),
                    bold = true,
                    text = stringResource(Res.string.leave_household),
                )
            }
        }

        if (members != null) {
            FriendlyText(text = stringResource(Res.string.list_household_members))

            members.forEach {
                FriendlyText(text = "* " + it, bold = true)
            }
        }
    }
}