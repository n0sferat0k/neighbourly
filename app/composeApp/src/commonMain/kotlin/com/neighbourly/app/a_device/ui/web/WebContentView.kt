package com.neighbourly.app.a_device.ui.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebGallery
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebMap

@Composable
fun WebContentView(
    modifier: Modifier,
    navigation: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val navState by navigation.state.collectAsState()

    navState.webContent.let {
        when (it) {
            WebMap -> WebMapView(modifier)
            is WebGallery -> WebGalleryView(
                modifier = modifier,
                itemId = it.itemId,
                imageId = it.imageId
            )
        }
    }
}
