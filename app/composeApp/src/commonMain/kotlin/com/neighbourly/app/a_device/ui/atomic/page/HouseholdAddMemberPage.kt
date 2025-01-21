package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.HouseholdAddMemberTemplate
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.HouseholdAddMemberViewModel

@Composable
fun HouseholdAddMemberPage(
    id: Int,
    username: String,
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: HouseholdAddMemberViewModel = viewModel { KoinProvider.KOIN.get<HouseholdAddMemberViewModel>() },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(id, username) {
        viewModel.loadProfile(id, username)
    }
    LaunchedEffect(state.added) {
        if (state.added) {
            navigationViewModel.goToHouseholdInfoEdit()
        }
    }

    HouseholdAddMemberTemplate(
        state = state,
        onAddToHousehold = viewModel::onAddToHousehold
    )
}
