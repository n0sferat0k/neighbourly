package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.template.NeighbourhoodAddMemberTemplate
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.profile.NeighbourhoodAddMemberViewModel

@Composable
fun NeighbourhoodAddMemberPage(
    neighbourhoodid: Int,
    id: Int,
    username: String,
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: NeighbourhoodAddMemberViewModel = viewModel { KoinProvider.KOIN.get<NeighbourhoodAddMemberViewModel>() },
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(neighbourhoodid, id, username) {
        viewModel.loadProfile(neighbourhoodid, id, username)
    }

    LaunchedEffect(state.added) {
        if (state.added) {
            navigationViewModel.goToNeighbourhoodInfoEdit()
        }
    }

    NeighbourhoodAddMemberTemplate(state, onAddToNeighbourhood = viewModel::onAddToNeighbourhood)
}
