package com.neighbourly.app.b_adapt.viewmodel.navigation

import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.ProfileContent.ProfileInfoEdit
import com.neighbourly.app.b_adapt.viewmodel.navigation.WebContent.WebMap

sealed class NavPages(val updater: (NavigationViewState) -> NavigationViewState) {

    object ShowMenuAndReset : NavPages({
        it.copy(
            mainContentVisible = true,
            mainContent = MainMenu,
            profileContent = ProfileInfoEdit,
            webContent = WebMap,
            addingNewHousehold = false,
        )
    })

    object HideMenu : NavPages({
        it.copy(mainContentVisible = false)
    })
}