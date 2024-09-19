package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darkrockstudios.libraries.mpfilepicker.MultipleFilePicker
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.BoxContent
import com.neighbourly.app.a_device.ui.BoxFooter
import com.neighbourly.app.a_device.ui.BoxHeader
import com.neighbourly.app.a_device.ui.ErrorText
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileViewModel
import com.neighbourly.app.loadContentsFromFile

@Composable
fun Profile(viewModel: ProfileViewModel = viewModel { KoinProvider.KOIN.get<ProfileViewModel>() }) {
    val state by viewModel.state.collectAsState()
    var showFilePicker by remember { mutableStateOf(false) }
    var contentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    MultipleFilePicker(show = showFilePicker, fileExtensions = listOf("jpg", "png")) { file ->
        showFilePicker = false

        file?.get(0)?.platformFile?.toString()?.let {
            viewModel.onProfileImageUpdate(loadContentsFromFile(it))
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BoxHeader(Modifier.align(Alignment.Start), busy = state.loading)

        BoxContent(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProfileMenu(imageUpdating = state.imageUpdating) { indexSelected ->
                    if (indexSelected == 0 && indexSelected == contentIndex) {
                        showFilePicker = true
                    } else {
                        contentIndex = indexSelected
                    }
                }

                if (state.error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ErrorText(state.error)
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (contentIndex) {
                    -1 -> HouseholdBarcodeScanner()
                    0 -> ProfileInfoEdit()
                    1 ->
                        HouseholdInfoEdit {
                            contentIndex = -1
                        }

                    2 -> HouseholdLocalize()
                    3 -> NeighbourhoodInfoEdit()
                }
            }
        }
        BoxFooter(modifier = Modifier.align(Alignment.End)) {
            ProfileFooter()
        }
    }
}
