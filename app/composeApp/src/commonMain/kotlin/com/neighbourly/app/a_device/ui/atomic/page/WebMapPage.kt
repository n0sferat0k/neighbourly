package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.neighbourly.app.GeoLocationCallback
import com.neighbourly.app.GetLocation
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.WebMapTemplate
import com.neighbourly.app.b_adapt.viewmodel.WebMapViewModel
import com.neighbourly.app.b_adapt.viewmodel.bean.GpsItemVS
import com.neighbourly.app.b_adapt.viewmodel.bean.toItemType
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel


@Composable
fun WebMapPage(
    viewModel: WebMapViewModel = androidx.lifecycle.viewmodel.compose.viewModel { KoinProvider.KOIN.get<WebMapViewModel>() },
    navigation: NavigationViewModel = androidx.lifecycle.viewmodel.compose.viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val state by viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onMapReady(false)
        }
    }

    WebMapTemplate(
        state = state,
        onMapReady = { viewModel.onMapReady(true) },
        onHouseAndTypeSelected = { type, id ->
            navigation.goToFindItems(type, id)
        },
        onDrawnUpdate = { drawData ->
            viewModel.onDrawn(drawData)
            navigation.goToNeighbourhoodInfoEdit()
        }
    )
}


