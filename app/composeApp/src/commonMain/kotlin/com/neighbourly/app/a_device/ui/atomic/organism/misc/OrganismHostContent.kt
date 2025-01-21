package com.neighbourly.app.a_device.ui.atomic.organism.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.CrownMenuItem
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.HomeMenuItem
import com.neighbourly.app.a_device.ui.atomic.organism.menu.MenuTabVS
import com.neighbourly.app.a_device.ui.atomic.organism.menu.OrganismOverlayMenu
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismUnderConstruction
import com.neighbourly.app.a_device.ui.atomic.page.BackendInfoPage
import com.neighbourly.app.a_device.ui.atomic.page.BoxManagementPage
import com.neighbourly.app.a_device.ui.atomic.page.FilteredItemListPage
import com.neighbourly.app.a_device.ui.atomic.page.ItemDetailsPage
import com.neighbourly.app.a_device.ui.atomic.page.LandingPage
import com.neighbourly.app.a_device.ui.atomic.page.LoginOrRegisterPage
import com.neighbourly.app.a_device.ui.atomic.page.ProfilePage
import com.neighbourly.app.a_device.ui.atomic.page.RemindersPage
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BackendInfo
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BoxManage
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.FindItems
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageMyStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageProfile
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.PublishStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.Reminders
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ShowItemDetails

@Composable
fun OrganismHostContent(
    showLanding: Boolean,
    showAuth: Boolean,
    showContent: Boolean,
    mainContent: MainContent,
    isOnline: Boolean,
    onCrownClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: (tab: MenuTabVS) -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        CrownMenuItem(isOnline = isOnline) {
            onCrownClick()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        HomeMenuItem(onHomeClick)

        when {
            showLanding -> LandingPage()
            showAuth -> LoginOrRegisterPage()
            showContent ->
                when (mainContent) {
                    MainMenu -> OrganismOverlayMenu(onSelect = onMenuClick)
                    is FindItems ->
                        mainContent.let {
                            FilteredItemListPage(
                                type = it.type,
                                householdId = it.householdId,
                                showExpired = false
                            )
                        }

                    ManageMyStuff ->
                        FilteredItemListPage(
                            type = null,
                            showOwnHousehold = true,
                            showExpired = true
                        )

                    ManageProfile -> ProfilePage()
                    PublishStuff -> ItemDetailsPage(null)
                    BoxManage -> BoxManagementPage()
                    is ShowItemDetails -> ItemDetailsPage(mainContent.itemId)
                    BackendInfo -> BackendInfoPage()
                    Reminders -> RemindersPage()
                }

            else -> OrganismUnderConstruction()
        }
    }
}
