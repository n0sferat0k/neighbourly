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

@Composable
fun BoxManagementPage(viewModel: BoxManagementViewModel = viewModel { KoinProvider.KOIN.get<BoxManagementViewModel>() }) {
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
        state,
        viewModel::addBox,
        viewModel::removeBox,
        viewModel::refresh,
        viewModel::updateName,
        viewModel::saveBox,
        viewModel::clearBox,
        viewModel::openBox,
        viewModel::unlockBox,
        viewModel::lightBox
    )
}