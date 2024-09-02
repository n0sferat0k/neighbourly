package com.neighbourly.app.ui

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.neighbourly.app.getPhoneNumber
import com.neighbourly.app.loadImageFromFile
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.app_name
import neighbourly.composeapp.generated.resources.confirmpassword
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.houses
import neighbourly.composeapp.generated.resources.login
import neighbourly.composeapp.generated.resources.password
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.profile
import neighbourly.composeapp.generated.resources.register
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun LoginOrRegister() {
    var index by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier.align(Alignment.TopStart).padding(start = 10.dp)) {
            Image(
                modifier = Modifier.size(48.dp).align(Alignment.CenterVertically),
                painter = painterResource(Res.drawable.houses),
                colorFilter = ColorFilter.tint(AppColors.primary),
                contentDescription = null
            )
            Text(
                modifier = Modifier.align(Alignment.Bottom).padding(start = 5.dp),
                text = stringResource(Res.string.app_name),
                style = TextStyle(
                    fontFamily = font(),
                    fontSize = 24.sp,
                    color = AppColors.primary
                )
            )
        }

        when (index) {
            0 -> Login()
            1 -> Register()
        }

        Row(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 10.dp, end = 20.dp)) {
            Text(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .clickable(onClick = {
                        index = 0
                    }),
                text = stringResource(Res.string.login),
                style = TextStyle(
                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                    fontFamily = font(),
                    fontSize = 20.sp,
                    color = AppColors.primary
                )
            )
            Text(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .clickable(onClick = {
                        index = 1
                    }),
                text = stringResource(Res.string.register),
                style = TextStyle(
                    fontWeight = if (index == 1) FontWeight.Bold else FontWeight.Normal,
                    fontFamily = font(),
                    fontSize = 20.sp,
                    color = AppColors.primary
                )
            )
        }
    }
}

@Composable
fun Login() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .widthIn(max = 400.dp)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(Res.string.username)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(Res.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = { /* Handle registration */ },
            modifier = Modifier
                .wrapContentWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary)
        ) {
            Text(
                stringResource(Res.string.login),
                color = Color.White,
                style = TextStyle(
                    fontFamily = font(),
                    fontSize = 18.sp,
                    color = AppColors.primary
                )
            )
        }
    }
}

@Composable
fun Register() {
    val defaultProfile = painterResource(Res.drawable.profile)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf(getPhoneNumber()) }
    var profileImage by remember { mutableStateOf<Painter?>(null) }

    var showFilePicker by remember { mutableStateOf(false) }


    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.get(0)?.let {
            profileImage = loadImageFromFile(it)
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .widthIn(max = 400.dp)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        profileImage.let {
            if (it == null) {
                Image(
                    painter = defaultProfile,
                    contentDescription = "Profile Image",
                    colorFilter = ColorFilter.tint(AppColors.primary),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp).clickable {
                        showFilePicker = true
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(2.dp, AppColors.primary, CircleShape)
                        .clickable {
                            showFilePicker = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = it,
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(80.dp).clip(CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username Input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(Res.string.username)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(Res.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(0.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { password = it },
            label = { Text(stringResource(Res.string.confirmpassword)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Full Name Input
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(stringResource(Res.string.fullname)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(stringResource(Res.string.phone)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = { /* Handle registration */ },
            modifier = Modifier
                .wrapContentWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = AppColors.primary)
        ) {
            Text(
                stringResource(Res.string.register),
                color = Color.White,
                style = TextStyle(
                    fontFamily = font(),
                    fontSize = 18.sp,
                    color = AppColors.primary
                )
            )
        }
    }
}