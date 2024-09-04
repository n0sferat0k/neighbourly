package com.neighbourly.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KoinProvider.initKoin()

        mainActivity = this
        setContent {
            App()
        }
    }

    companion object {
        lateinit var mainActivity: Activity
        val locationProvider: FusedLocationProviderClient get() =
            LocationServices.getFusedLocationProviderClient(mainActivity)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
