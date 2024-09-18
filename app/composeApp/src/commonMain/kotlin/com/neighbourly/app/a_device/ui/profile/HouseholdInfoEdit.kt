package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.ErrorText
import com.neighbourly.app.a_device.ui.font
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdInfoEditViewModel
import com.neighbourly.app.loadContentsFromFile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.about
import neighbourly.composeapp.generated.resources.address
import neighbourly.composeapp.generated.resources.householdName
import neighbourly.composeapp.generated.resources.houses
import neighbourly.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HouseholdInfoEdit(viewModel: HouseholdInfoEditViewModel = viewModel { KoinProvider.KOIN.get<HouseholdInfoEditViewModel>() }) {
    val state by viewModel.state.collectAsState()
    val defaultHouseImg = painterResource(Res.drawable.houses)
    var showFilePicker by remember { mutableStateOf(false) }

    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.get(0)?.platformFile?.toString()?.let {
            viewModel.onHouseholdImageUpdate(loadContentsFromFile(it))
        }
    }

    Row {
        // Name Input
        OutlinedTextField(
            value = state.nameOverride ?: state.name,
            onValueChange = {
                viewModel.updateName(it)
            },
            label = { Text(stringResource(Res.string.householdName)) },
            isError = state.nameError,
            modifier = Modifier.weight(1f),
        )

        if (state.hasHousehold) {
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
        onValueChange = {
            viewModel.updateAbout(it)
        },
        maxLines = 5,
        label = { Text(stringResource(Res.string.about)) },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Save Button
    Button(
        onClick = {
            viewModel.onSaveHousehold()
        },
        modifier =
            Modifier
                .wrapContentWidth()
                .height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary),
    ) {
        if (state.saving) {
            CircularProgressIndicator(
                modifier =
                    Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        }
        Text(
            stringResource(Res.string.save),
            color = Color.White,
            style =
                TextStyle(
                    fontFamily = font(),
                    fontSize = 18.sp,
                    color = AppColors.primary,
                ),
        )
    }

    if (state.error.isNotEmpty()) {
        ErrorText(state.error)
    }
}
