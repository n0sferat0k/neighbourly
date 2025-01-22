package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.NeighbourhoodInfoEditTemplate
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodInfoViewModel

@Composable
fun NeighbourhoodInfoEditPage(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: NeighbourhoodInfoViewModel = viewModel { KoinProvider.KOIN.get<NeighbourhoodInfoViewModel>() },
) {
    val state by viewModel.state.collectAsState()

    NeighbourhoodInfoEditTemplate(
        state = state,
        onAddMember = navigationViewModel::goToScanMemberHouseholdForNeighbourhood,
        onLeave = viewModel::leaveNeighbourhood,
        onCreateNew = {
            viewModel.startNeighbourhoodCreation()
            navigationViewModel.goToMap()
        },
        onSaveNew = viewModel::completeNeighbourhoodCreation,
        onCancelNew = viewModel::cancelNeighbourhoodCreation
    )
}
