package com.neighbourly.app.a_device.ui.profile

import androidx.compose.runtime.Composable
import com.neighbourly.app.a_device.ui.BarcodeScanner

@Composable
fun HouseholdBarcodeScanner() {
    BarcodeScanner {
        println("AAAAAAAAAAAAAA " + it)
    }
}
