package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.HouseholdInfoEditTemplate
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdInfoEditViewModel

@Composable
fun HouseholdInfoEditPage(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: HouseholdInfoEditViewModel = viewModel { KoinProvider.KOIN.get<HouseholdInfoEditViewModel>() },
) {
    val state by viewModel.state.collectAsState()
    val navigationState by navigationViewModel.state.collectAsState()

    HouseholdInfoEditTemplate(
        state = state,
        navigationState = navigationState,
        onCreateHousehold = navigationViewModel::goToAddHousehold,
        onHouseholdImageUpdate = viewModel::onHouseholdImageUpdate,
        onSaveHousehold = viewModel::onSaveHousehold,
        onAddMember = navigationViewModel::goToScanMemberForHousehold,
        onLeaveHousehold = viewModel::onLeaveHousehold
    )
}
