package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.BarcodeScanner

@Composable
fun HouseholdBarcodeScanner() {
    BarcodeScanner(modifier = Modifier.fillMaxSize()) {
    }
}
