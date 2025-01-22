package com.neighbourly.app.a_device.ui.atomic.template

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.molecule.misc.ScreenSplitter
import com.neighbourly.app.a_device.ui.atomic.organism.menu.MenuTabVS
import com.neighbourly.app.a_device.ui.atomic.organism.misc.OrganismHostContent
import com.neighbourly.app.a_device.ui.atomic.organism.misc.OrganismHostContentOverWeb
import com.neighbourly.app.a_device.ui.atomic.page.WebContentPage
import com.neighbourly.app.b_adapt.viewmodel.AppStateInfoViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewState

@Composable
fun HostTemplate(
    appState: AppStateInfoViewModel.AppStateInfoViewState,
    navigationState: NavigationViewState,
    onCrownClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: (tab: MenuTabVS) -> Unit,
) {
    if (appState.isWideLand) {
        ScreenSplitter(
            leftContent = {
                OrganismHostContent(
                    showLanding = appState.isLanding,
                    showAuth = !appState.isLoggedIn,
                    showContent = navigationState.mainContentVisible,
                    mainContent = navigationState.mainContent,
                    isOnline = appState.isOnline,
                    onCrownClick = onCrownClick,
                    onHomeClick = onHomeClick,
                    onMenuClick = onMenuClick,
                )
            },
            rightContent = {
                WebContentPage()
            }
        )
    } else {
        OrganismHostContentOverWeb(
            showLanding = appState.isLanding,
            showAuth = !appState.isLoggedIn,
            showContent = navigationState.mainContentVisible,
            mainContent = navigationState.mainContent,
            isOnline = appState.isOnline,
            onCrownClick = onCrownClick,
            onHomeClick = onHomeClick,
            onMenuClick = onMenuClick,
        )
    }
}