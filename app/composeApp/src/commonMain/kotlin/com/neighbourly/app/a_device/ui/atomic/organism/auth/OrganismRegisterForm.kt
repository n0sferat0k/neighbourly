package com.neighbourly.app.a_device.ui.atomic.organism.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import com.neighbourly.app.a_device.ui.AppColors
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyErrorText
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyText
import com.neighbourly.app.d_entity.util.isValidEmail
import com.neighbourly.app.d_entity.util.isValidPhone
import com.neighbourly.app.getPhoneNumber
import com.neighbourly.app.loadImageFromFile
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.confirmpassword
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.password
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.profile
import neighbourly.composeapp.generated.resources.register
import neighbourly.composeapp.generated.resources.remember_me
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismRegisterForm(
    loading: Boolean,
    error: String,
    onRegister: (
        username: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        email: String,
        phoneNumber: String,
        profileFile: String?,
        remember: Boolean
    ) -> Unit
) {
    val defaultProfile = painterResource(Res.drawable.profile)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf(getPhoneNumber().orEmpty()) }
    var profileImage by remember { mutableStateOf<Painter?>(null) }
    var profileFile by remember { mutableStateOf<String?>(null) }
    var showFilePicker by remember { mutableStateOf(false) }
    var remember by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    FilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.platformFile.toString().let {
            profileImage = loadImageFromFile(it)
            profileFile = it
        }
    }

    Column(
        modifier =
        Modifier
            .widthIn(max = 400.dp),
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

            },
            label = { Text(stringResource(Res.string.username)) },
            isError = username.isBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text(stringResource(Res.string.password)) },
            isError = (password.isBlank() || password != confirmPassword),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(0.dp),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
            },
            label = { Text(stringResource(Res.string.confirmpassword)) },
            isError = (password.isBlank() || password != confirmPassword),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Hide confirm password" else "Show confirm password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Full Name Input
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
            },
            label = { Text(stringResource(Res.string.fullname)) },
            isError = fullName.isBlank(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = { Text(stringResource(Res.string.email)) },
            isError = !email.isValidEmail(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number Input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
            },
            label = { Text(stringResource(Res.string.phone)) },
            isError = !phoneNumber.isValidPhone(),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            FriendlyText(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(Res.string.remember_me)
            )
            Checkbox(
                checked = remember,
                onCheckedChange = { remember = it },
                colors = CheckboxDefaults.colors(checkedColor = AppColors.primary)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FriendlyButton(text = stringResource(Res.string.register), loading = loading) {
            onRegister(
                username,
                password,
                confirmPassword,
                fullName,
                email,
                phoneNumber,
                profileFile,
                remember
            )
        }

        if (error.isNotEmpty()) {
            FriendlyErrorText(error)
        }
    }
}
