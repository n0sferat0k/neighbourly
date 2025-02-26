package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val lifecycleOwner = LocalLifecycleOwner.current

    var monitor by remember { mutableStateOf(false) }
    val boxIds by derivedStateOf { state.boxes.map { it.id } }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                ON_PAUSE -> monitor = false
                ON_RESUME -> monitor = true
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.monitor(false)
        }
    }

    LaunchedEffect(monitor, boxIds) {
        viewModel.monitor(monitor, boxIds)
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