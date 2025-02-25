package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.AiInterfaceTemplate
import com.neighbourly.app.b_adapt.viewmodel.ai.AiInterfaceViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun AiInterfacePage(
    viewModel: AiInterfaceViewModel = viewModel { KoinProvider.KOIN.get<AiInterfaceViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val state by viewModel.state.collectAsState()
    AiInterfaceTemplate(
        state = state,
        onPrompt = viewModel::onPrompt,
        onClose = navigationViewModel::goHome,
        onItemSelect = navigationViewModel::goToItemDetails,
        onHouseholdSelect = navigationViewModel::goToHouseholdDetails
    )
}