package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.HouseholdLocalizeTemplate
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdLocalizeViewModel

@Composable
fun HouseholdLocalizePage(viewModel: HouseholdLocalizeViewModel = viewModel { KoinProvider.KOIN.get<HouseholdLocalizeViewModel>() }) {
    val state by viewModel.state.collectAsState()

    HouseholdLocalizeTemplate(
        state = state,
        onStartLocalize = viewModel::onStartLocalize,
        onStopLocalize = viewModel::onStopLocalize,
        onRelocate = viewModel::onRelocate,
        onAcceptLocalize = viewModel::onAcceptLocalize,
        onRetryLocalize = viewModel::onRetryLocalize
    )
}
