package com.neighbourly.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import com.neighbourly.app.a_device.ui.atomic.page.HostPage
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                requestPermissions()
                HostPage()
            }
        }
    }

    override fun onBackPressed() {
        if (KoinProvider.KOIN.get<NavigationViewModel>().goBack() != true) {
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(EXTRA_ITEM_ID)) {
            KoinProvider.KOIN.get<NavigationViewModel>()
                .goToItemDetails(intent.getIntExtra(EXTRA_ITEM_ID, 0))
        }

    }

    companion object {
        const val EXTRA_ITEM_ID = "extra.item.id"
    }
}
