package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.BoxManagementTemplate
import com.neighbourly.app.b_adapt.viewmodel.box.BoxManagementViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun BoxManagementPage(
    viewModel: BoxManagementViewModel = viewModel { KoinProvider.KOIN.get<BoxManagementViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.boxes) {
        viewModel.monitor(state.boxes.map { it.id })
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.monitor(null)
        }
    }

    BoxManagementTemplate(
        state = state,
        addBox = viewModel::addBox,
        editBox = viewModel::editBox,
        removeBox = viewModel::removeBox,
        refresh = viewModel::refresh,
        saveBox = viewModel::saveBox,
        saveBoxShare = viewModel::saveBoxShare,
        clearBox = viewModel::clearBox,
        openBox = viewModel::openBox,
        unlockBox = viewModel::unlockBox,
        lightBox = viewModel::lightBox,
        shareBox = viewModel::shareBox,
        shareBoxSelect = viewModel::shareBoxSelect,
        shareBoxDelete = viewModel::shareBoxDelete,
        onHouseholdClick = navigationViewModel::goToHouseholdDetails
    )
}