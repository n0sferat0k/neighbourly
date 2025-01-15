package com.neighbourly.app.a_device.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardFooter
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardHeader
import com.neighbourly.app.a_device.ui.atomic.molecule.card.CardScrollableContent
import com.neighbourly.app.a_device.ui.atomic.molecule.card.LogoutCardFooter
import com.neighbourly.app.a_device.ui.atomic.molecule.card.RoundedCornerCard
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdAddMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdLocalize
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.HouseholdScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodAddMemberHousehold
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.NeighbourhoodScanMember
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.profile.ProfileViewModel

@Composable
fun Profile(
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
    viewModel: ProfileViewModel = viewModel { KoinProvider.KOIN.get<ProfileViewModel>() },
) {
    val navigation by navigationViewModel.state.collectAsState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    RoundedCornerCard {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            CardHeader(Modifier.align(Alignment.Start), busy = state.loading) {
                viewModel.refresh()
            }

            CardScrollableContent(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ProfileMenu()

                    Spacer(modifier = Modifier.height(8.dp))
                    navigation.profileContent.let {
                        when (it) {
                            ProfileInfoEdit -> ProfileInfoEditView()
                            HouseholdInfoEdit -> HouseholdInfoEditView()
                            HouseholdLocalize -> HouseholdLocalizeView()
                            NeighbourhoodInfoEdit -> NeighbourhoodInfoEditView()
                            is HouseholdAddMember -> HouseholdAddMemberView(it.id, it.username)
                            HouseholdScanMember -> HouseholdBarcodeScannerView()
                            is NeighbourhoodScanMember -> NeighbourhoodBarcodeScannerView(it.neighbourhoodid)
                            is NeighbourhoodAddMemberHousehold -> NeighbourhoodAddMemberView(
                                it.neighbourhoodid,
                                it.id,
                                it.username
                            )
                        }
                    }
                }
            }
            CardFooter {
                LogoutCardFooter(viewModel::onLogout)
            }
        }
    }
}
