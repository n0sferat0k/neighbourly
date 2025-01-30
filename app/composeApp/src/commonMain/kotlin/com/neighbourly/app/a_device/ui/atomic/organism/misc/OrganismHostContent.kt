package com.neighbourly.app.a_device.ui.atomic.organism.misc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neighbourly.app.a_device.ui.atomic.molecule.menu.HomeMenuItem
import com.neighbourly.app.a_device.ui.atomic.organism.menu.OrganismCrownMenu
import com.neighbourly.app.a_device.ui.atomic.organism.menu.OrganismOverlayMenuFull
import com.neighbourly.app.a_device.ui.atomic.organism.menu.OrganismOverlayMenuLimited
import com.neighbourly.app.a_device.ui.atomic.page.AiInterfacePage
import com.neighbourly.app.a_device.ui.atomic.page.BackendInfoPage
import com.neighbourly.app.a_device.ui.atomic.page.BoxManagementPage
import com.neighbourly.app.a_device.ui.atomic.page.FilteredItemListPage
import com.neighbourly.app.a_device.ui.atomic.page.HouseholdDetailPage
import com.neighbourly.app.a_device.ui.atomic.page.ItemDetailsPage
import com.neighbourly.app.a_device.ui.atomic.page.LandingPage
import com.neighbourly.app.a_device.ui.atomic.page.LoginOrRegisterPage
import com.neighbourly.app.a_device.ui.atomic.page.ProfilePage
import com.neighbourly.app.a_device.ui.atomic.page.RemindersPage
import com.neighbourly.app.b_adapt.viewmodel.bean.MenuTabVS
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.AiInterface
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BackendInfo
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BoxManage
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.FindItems
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.HouseholdDetails
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ItemDetails
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageMyStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageProfile
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.PublishStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.Reminders

@Composable
fun OrganismHostContent(
    showLanding: Boolean,
    showAuth: Boolean,
    showContent: Boolean,
    mainContent: MainContent,
    showLimitedContent: Boolean,
    isOnline: Boolean,
    isAiOnline: Boolean,
    onWifiClick: () -> Unit,
    onAiClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: (tab: MenuTabVS) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        OrganismCrownMenu(
            isOnline = isOnline,
            isAiOnline = isAiOnline,
            onWifiClick = onWifiClick,
            onAiClick = onAiClick
        )
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
                    MainMenu -> if (showLimitedContent)
                        OrganismOverlayMenuLimited(onSelect = onMenuClick)
                    else
                        OrganismOverlayMenuFull(onSelect = onMenuClick)

                    is HouseholdDetails -> HouseholdDetailPage(mainContent.householdId)

                    is FindItems ->
                        mainContent.let {
                            FilteredItemListPage(
                                type = it.type,
                                householdId = it.householdId,
                                itemIds = it.itemIds,
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
                    is PublishStuff -> ItemDetailsPage(null, mainContent.type)
                    BoxManage -> BoxManagementPage()
                    is ItemDetails -> ItemDetailsPage(mainContent.itemId)
                    BackendInfo -> BackendInfoPage()
                    AiInterface -> AiInterfacePage()
                    Reminders -> RemindersPage()
                }
        }
    }
}
