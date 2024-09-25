package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.BarcodeScanner
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun HouseholdBarcodeScanner(navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }) {
    BarcodeScanner(modifier = Modifier.fillMaxSize()) { scanString ->
        scanString.split(",").let { tokens ->
            runCatching {
                navigationViewModel.goToHouseholdAddMember(tokens[0].toInt(), tokens[1])
            }
        }
    }
}
