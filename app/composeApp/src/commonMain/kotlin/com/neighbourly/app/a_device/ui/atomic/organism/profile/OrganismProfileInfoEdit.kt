package com.neighbourly.app.a_device.ui.atomic.organism.profile

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neighbourly.app.a_device.ui.atomic.atom.FriendlyButton
import com.neighbourly.app.b_adapt.viewmodel.bean.ProfileVS
import com.neighbourly.app.d_entity.util.isValidEmail
import com.neighbourly.app.d_entity.util.isValidPhone
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.about
import neighbourly.composeapp.generated.resources.email
import neighbourly.composeapp.generated.resources.fullname
import neighbourly.composeapp.generated.resources.phone
import neighbourly.composeapp.generated.resources.save
import neighbourly.composeapp.generated.resources.username
import org.jetbrains.compose.resources.stringResource

@Composable
fun OrganismProfileInfoEdit(
    profile: ProfileVS,
    saving: Boolean,
    onSave: (
        fullnameOverride: String?,
        emailOverride: String?,
        phoneOverride: String?,
        aboutOverride: String?,
    ) -> Unit,
) {
    var fullnameOverride by remember { mutableStateOf<String?>(null) }
    var emailOverride by remember { mutableStateOf<String?>(null) }
    var phoneOverride by remember { mutableStateOf<String?>(null) }
    var aboutOverride by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profile) {
        fullnameOverride = null
        emailOverride = null
        phoneOverride = null
        aboutOverride = null
    }

    val hasChanged by derivedStateOf {
        listOf(
            fullnameOverride,
            emailOverride,
            phoneOverride,
            aboutOverride,
        ).any { it != null }
    }

    // Username Input
    OutlinedTextField(
        value = profile.username,
        onValueChange = { },
        enabled = false,
        label = { Text(stringResource(Res.string.username)) },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Full Name Input
    OutlinedTextField(
        value = fullnameOverride ?: profile.fullname,
        onValueChange = {
            fullnameOverride = it
        },
        label = { Text(stringResource(Res.string.fullname)) },
        isError = fullnameOverride?.isBlank() ?: false,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Email Input
    OutlinedTextField(
        value = emailOverride ?: profile.email,
        onValueChange = {
            emailOverride = it
        },
        label = { Text(stringResource(Res.string.email)) },
        isError = emailOverride?.let { it.isBlank() || !it.isValidEmail() } ?: false,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Phone Number Input
    OutlinedTextField(
        value = phoneOverride ?: profile.phone,
        onValueChange = {
            phoneOverride = it
        },
        label = { Text(stringResource(Res.string.phone)) },
        isError = phoneOverride?.let { it.isBlank() || !it.isValidPhone() } ?: false,
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // About Input
    OutlinedTextField(
        value = aboutOverride ?: profile.about,
        onValueChange = {
            aboutOverride = it
        },
        maxLines = 5,
        label = { Text(stringResource(Res.string.about)) },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (hasChanged) {
        FriendlyButton(
            text = stringResource(Res.string.save),
            loading = saving,
        ) {
            onSave(
                fullnameOverride,
                emailOverride,
                phoneOverride,
                aboutOverride,
            )
        }
    }
}
