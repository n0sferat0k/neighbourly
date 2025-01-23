package com.neighbourly.app.a_device.ui.atomic.page

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS
import com.neighbourly.app.a_device.ui.atomic.template.HostTemplate
import com.neighbourly.app.b_adapt.viewmodel.AppStateInfoViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BoxManage
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageMyStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.PublishStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.Reminders
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun HostPage(
    appStateInfoViewModel: AppStateInfoViewModel = viewModel { KoinProvider.KOIN.get<AppStateInfoViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() },
) {
    val navigationState by navigationViewModel.state.collectAsState()
    val appState by appStateInfoViewModel.state.collectAsState()

    LaunchedEffect(appState.isWideLand) {
        navigationViewModel.disableMainToggle(appState.isWideLand)
    }

    HostTemplate(appState = appState, navigationState = navigationState,
        onCrownClick = {
            navigationViewModel.goToBackendInfo()
        }, onHomeClick = {
            navigationViewModel.toggleMainContent()
        },
        onMenuClick = {
            when (it) {
                MenuTabVS.PROFILE -> navigationViewModel.goToProfile()
                MenuTabVS.MYSTUFF -> navigationViewModel.goToMainPage(ManageMyStuff)
                MenuTabVS.BOX -> navigationViewModel.goToMainPage(BoxManage)
                MenuTabVS.PUBLISH -> navigationViewModel.goToMainPage(PublishStuff)
                MenuTabVS.REMINDERS -> navigationViewModel.goToMainPage(Reminders)
                is MenuTabVS.ITEMS -> navigationViewModel.goToFindItems(it.type)
            }
        }
    )
}