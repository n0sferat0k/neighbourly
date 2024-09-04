package com.neighbourly.app.a_device.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.b_adapt.viewmodel.ProfileViewModel
import com.neighbourly.app.loadImageFromFile
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.profile
import org.jetbrains.compose.resources.painterResource

@Composable
fun Profile(registerViewModel: ProfileViewModel = viewModel { KoinProvider.KOIN.get<ProfileViewModel>() }) {
    val state by registerViewModel.state.collectAsState()

    val defaultProfile = painterResource(Res.drawable.profile)
    var profileImage by remember { mutableStateOf<Painter?>(null) }
    var showFilePicker by remember { mutableStateOf(false) }

    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.get(0)?.let {
            profileImage = loadImageFromFile(it)
            registerViewModel.onProfileImageUpdate(it)
        }
    }

    Column(
        modifier =
            Modifier
                .padding(20.dp)
                .widthIn(max = 400.dp)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        profileImage.let {
            if (it == null) {
                Image(
                    painter = defaultProfile,
                    contentDescription = "Profile Image",
                    colorFilter = ColorFilter.tint(AppColors.primary),
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier.size(80.dp).clickable {
                            showFilePicker = true
                        },
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .size(80.dp)
                            .border(2.dp, AppColors.primary, CircleShape)
                            .clickable {
                                showFilePicker = true
                            },
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = it,
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(80.dp).clip(CircleShape),
                    )
                }
            }
        }
    }
}
