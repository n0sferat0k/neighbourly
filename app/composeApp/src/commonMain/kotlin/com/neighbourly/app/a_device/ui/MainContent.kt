package com.neighbourly.app.a_device.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.auth.LoginOrRegister
import com.neighbourly.app.a_device.ui.profile.Profile
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindBarters
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindDonations
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindEvents
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindNeeds
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindRequests
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindSales
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindSkillshare
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.ManageMyStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.ManageProfile
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.PublishStuff

@Composable
fun MainContent(navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }) {
    val navigation by navigationViewModel.state.collectAsState()

    if (navigation.restrictedContent) {
        AnimatedVisibility(navigation.mainContentVisible) {
            ContentBox {
                if (navigation.userLoggedIn) {
                    Profile()
                } else {
                    LoginOrRegister()
                }
            }
        }
    } else {
        if (navigation.mainContent == MainMenu) {
            FullMenuContent()
        }
        AnimatedVisibility(navigation.mainContent == ManageProfile) { ContentBox { Profile() } }
        AnimatedVisibility(navigation.mainContent == ManageMyStuff) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == PublishStuff) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == FindDonations) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == FindBarters) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == FindSales) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == FindEvents) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == FindNeeds) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == FindRequests) { ContentBox { UnderConstruction() } }
        AnimatedVisibility(navigation.mainContent == FindSkillshare) { ContentBox { UnderConstruction() } }
    }
}
