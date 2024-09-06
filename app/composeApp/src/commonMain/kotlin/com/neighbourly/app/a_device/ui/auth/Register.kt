package com.neighbourly.app.a_device.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.font
import com.neighbourly.app.b_adapt.viewmodel.RegisterViewModel
import com.neighbourly.app.d_entity.data.FileContents
import com.neighbourly.app.getPhoneNumber
import com.neighbourly.app.loadContentsFromFile
import com.neighbourly.app.loadImageFromFile
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirmpassword
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.password
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.profile
import neighbourly.composeapp.generated.resources.register
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Register(registerViewModel: RegisterViewModel = viewModel { KoinProvider.KOIN.get<RegisterViewModel>() }) {
    val state by registerViewModel.state.collectAsState()

    val defaultProfile = painterResource(Res.drawable.profile)
    var username by remember { mutableStateOf("nosfi") }
    var password by remember { mutableStateOf("nosfi") }
    var confirmPassword by remember { mutableStateOf("nosfi") }
    var fullName by remember { mutableStateOf("nos fi") }
    var email by remember { mutableStateOf("nos@fi.com") }
    var phoneNumber by remember { mutableStateOf(getPhoneNumber()) }
    var profileImage by remember { mutableStateOf<Painter?>(null) }
    var profileFile by remember { mutableStateOf<FileContents?>(null) }
    var showFilePicker by remember { mutableStateOf(false) }

    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.get(0)?.platformFile.toString().let {
            profileImage = loadImageFromFile(it)
            profileFile = loadContentsFromFile(it)
        }
    }

    Column(
        modifier =
            Modifier
                .padding(20.dp)
                .widthIn(max = 400.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        profileImage.let {
            if (it == null) {
                Image(
                    painter = defaultProfile,
                    contentDescription = "Profile Image",
                    colorFilter = ColorFilter.tint(AppColors.primary),
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
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(80.dp).clip(CircleShape),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                registerViewModel.validateUsername(username)
            },
            label = { Text(stringResource(Res.string.username)) },
            isError = state.usernameError,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                registerViewModel.validatePassword(password, confirmPassword)
            },
            label = { Text(stringResource(Res.string.password)) },
            isError = state.passwordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(0.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                registerViewModel.validatePassword(password, confirmPassword)
            },
            label = { Text(stringResource(Res.string.confirmpassword)) },
            isError = state.passwordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Full Name Input
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                registerViewModel.validateFullname(fullName)
            },
            label = { Text(stringResource(Res.string.fullname)) },
            isError = state.fullnameError,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                registerViewModel.validateEmail(email)
            },
            label = { Text(stringResource(Res.string.email)) },
            isError = state.emailError,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                registerViewModel.validatePhone(phoneNumber)
            },
            label = { Text(stringResource(Res.string.phone)) },
            isError = state.phoneError,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = {
                registerViewModel.onRegister(
                    username,
                    password,
                    confirmPassword,
                    fullName,
                    email,
                    phoneNumber,
                    profileFile,
                )
            },
            modifier =
                Modifier
                    .wrapContentWidth()
                    .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary),
        ) {
            if (state.loading) {
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
                stringResource(Res.string.register),
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
