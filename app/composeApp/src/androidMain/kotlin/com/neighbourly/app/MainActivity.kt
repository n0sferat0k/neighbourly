package com.neighbourly.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    override fun onBackPressed() {
        if(KoinProvider.KOIN.get<NavigationViewModel>().goBack() != true) {
            super.onBackPressed()
        }
    }
}
