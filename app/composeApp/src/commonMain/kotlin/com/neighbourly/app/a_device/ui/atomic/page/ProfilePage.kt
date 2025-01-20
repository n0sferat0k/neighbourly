package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.ProfileTemplate
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileViewModel

@Composable
fun ProfilePage(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: ProfileViewModel = viewModel { KoinProvider.KOIN.get<ProfileViewModel>() },
) {
    val navigationState by navigationViewModel.state.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    ProfileTemplate(
        state = state,
        navigationState = navigationState,
        refresh = viewModel::refresh,
        logout = viewModel::onLogout,
        profileImageUpdate = viewModel::onProfileImageUpdate,
        profileInfoSave = viewModel::onSaveProfile,
        profileImageSelect = navigationViewModel::goToProfileInfoEdit,
        houseInfoSelect = navigationViewModel::goToHouseholdInfoEdit,
        houseLocationSelect = navigationViewModel::goToHouseholdLocalize,
        neighbourhoodInfoSelect = navigationViewModel::goToNeighbourhoodInfoEdit,
    )
}
