package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.neighbourly.app.a_device.ui.BoxContent
import com.neighbourly.app.a_device.ui.BoxHeader
import com.neighbourly.app.a_device.ui.CurlyText
import com.neighbourly.app.a_device.ui.font
import com.neighbourly.app.b_adapt.viewmodel.ProfileViewModel
import com.neighbourly.app.loadContentsFromFile
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.everywhere
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.here
import neighbourly.composeapp.generated.resources.home
import neighbourly.composeapp.generated.resources.logout
import neighbourly.composeapp.generated.resources.map
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.polygon
import neighbourly.composeapp.generated.resources.profile
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Profile(profileViewModel: ProfileViewModel = viewModel { KoinProvider.KOIN.get<ProfileViewModel>() }) {
    val state by profileViewModel.state.collectAsState()
    val defaultProfile = painterResource(Res.drawable.profile)
    var showFilePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.refresh()
    }

    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.get(0)?.platformFile.toString().let {
            profileViewModel.onProfileImageUpdate(loadContentsFromFile(it))
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start), busy = state.loading)

        BoxContent(modifier = Modifier.weight(1f)) {
            Column(
                modifier =
                    Modifier
                        .padding(20.dp)
                        .widthIn(max = 400.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    state.user.imageurl.let {
                        if (!it.isNullOrBlank() && !state.imageUpdating) {
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
                                KamelImage(
                                    modifier = Modifier.size(80.dp).clip(CircleShape),
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
                            }
                        } else if (state.imageUpdating) {
                            CircularProgressIndicator(color = AppColors.primary)
                        } else {
                            Image(
                                modifier = Modifier.size(80.dp),
                                painter = defaultProfile,
                                contentDescription = "Profile Image",
                                colorFilter = ColorFilter.tint(AppColors.primary),
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f).fillMaxHeight())

                    Image(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "Home",
                        contentScale = ContentScale.FillBounds,
                        colorFilter =
                            if (state.user.household == null) {
                                ColorFilter.tint(AppColors.complementary)
                            } else {
                                ColorFilter.tint(AppColors.primary)
                            },
                        modifier =
                            Modifier.size(48.dp).clickable {
                            },
                    )
                    Spacer(Modifier.width(14.dp).fillMaxHeight())
                    Image(
                        painter = painterResource(Res.drawable.map),
                        contentDescription = "Map",
                        contentScale = ContentScale.FillBounds,
                        colorFilter =
                            if (state.user.household?.isLocalized == true) {
                                ColorFilter.tint(AppColors.primary)
                            } else {
                                ColorFilter.tint(AppColors.complementary)
                            },
                        modifier =
                            Modifier.size(48.dp).clickable {
                            },
                    )
                    Spacer(Modifier.width(14.dp).fillMaxHeight())
                    Image(
                        painter = painterResource(Res.drawable.polygon),
                        contentDescription = "Map",
                        contentScale = ContentScale.FillBounds,
                        colorFilter =
                            if (state.user.neighbourhoods.isEmpty()) {
                                ColorFilter.tint(AppColors.complementary)
                            } else {
                                ColorFilter.tint(AppColors.primary)
                            },
                        modifier =
                            Modifier.size(48.dp).clickable {
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Username Input
                OutlinedTextField(
                    value = state.user.username,
                    onValueChange = { },
                    enabled = false,
                    label = { Text(stringResource(Res.string.username)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Full Name Input
                OutlinedTextField(
                    value = state.fullnameOverride ?: state.user.fullname,
                    onValueChange = {
                        profileViewModel.updateFullname(it)
                    },
                    label = { Text(stringResource(Res.string.fullname)) },
                    isError = state.fullnameError,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Email Input
                OutlinedTextField(
                    value = state.emailOverride ?: state.user.email,
                    onValueChange = {
                        profileViewModel.updateEmail(it)
                    },
                    label = { Text(stringResource(Res.string.email)) },
                    isError = state.emailError,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Phone Number Input
                OutlinedTextField(
                    value = state.phoneOverride ?: state.user.phone,
                    onValueChange = {
                        profileViewModel.updatePhone(it)
                    },
                    label = { Text(stringResource(Res.string.phone)) },
                    isError = state.phoneError,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Save Button
                Button(
                    onClick = {
                        profileViewModel.onSaveProfile()
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
                    Text(
                        text = state.error,
                        color = Color.Red,
                        style =
                            TextStyle(
                                fontFamily = font(),
                                fontSize = 18.sp,
                                color = AppColors.primary,
                            ),
                    )
                }
            }
        }
        Row(
            modifier = Modifier.align(Alignment.End).padding(bottom = 10.dp, end = 20.dp),
        ) {
            CurlyText(text = stringResource(Res.string.logout) + ":")
            CurlyText(
                modifier =
                    Modifier
                        .padding(start = 15.dp, end = 5.dp)
                        .clickable(onClick = {
                            profileViewModel.onLogout(logoutAll = false)
                        }),
                text = stringResource(Res.string.here),
                bold = true,
            )
            CurlyText(text = "/")
            CurlyText(
                modifier =
                    Modifier
                        .padding(start = 5.dp)
                        .clickable(onClick = {
                            profileViewModel.onLogout(logoutAll = true)
                        }),
                text = stringResource(Res.string.everywhere),
                bold = true,
            )
        }
    }
}
