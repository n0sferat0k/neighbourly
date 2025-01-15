package com.neighbourly.app.a_device.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.molecule.card.RoundedCornerCard
import com.neighbourly.app.a_device.ui.atomic.organism.menu.MenuTab
import com.neighbourly.app.a_device.ui.atomic.organism.menu.OrganismOverlayMenu
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismUnderConstruction
import com.neighbourly.app.a_device.ui.atomic.page.BackendInfoPage
import com.neighbourly.app.a_device.ui.atomic.page.BoxManagementPage
import com.neighbourly.app.a_device.ui.atomic.page.FilteredItemListPage
import com.neighbourly.app.a_device.ui.atomic.page.LoginOrRegisterPage
import com.neighbourly.app.a_device.ui.atomic.page.ItemDetailsPage
import com.neighbourly.app.a_device.ui.items.RemidersView
import com.neighbourly.app.a_device.ui.misc.LandingView
import com.neighbourly.app.a_device.ui.profile.Profile
import com.neighbourly.app.b_adapt.viewmodel.items.MainContentViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BackendInfo
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BoxManage
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.FindItems
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.MainMenu
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageMyStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageProfile
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.PublishStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.Reminders
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ShowItemDetails
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel

@Composable
fun MainContent(
    viewModel: MainContentViewModel = viewModel { KoinProvider.KOIN.get<MainContentViewModel>() },
    navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }
) {
    val state by viewModel.state.collectAsState()
    val navigation by navigationViewModel.state.collectAsState()
    if (state.landing) {
        LandingView()
    } else if (navigation.restrictedContent) {
        AnimatedVisibility(navigation.mainContentVisible) {
            if (navigation.userLoggedIn) {
                Profile()
            } else {
                LoginOrRegisterPage()
            }
        }
    } else {
        if (navigation.mainContent == MainMenu) {
            OrganismOverlayMenu {
                when (it) {
                    MenuTab.PROFILE -> navigationViewModel.goToProfile()
                    MenuTab.MYSTUFF -> navigationViewModel.goToMainPage(ManageMyStuff)
                    MenuTab.BOX -> navigationViewModel.goToMainPage(BoxManage)
                    MenuTab.PUBLISH -> navigationViewModel.goToMainPage(PublishStuff)
                    MenuTab.REMINDERS -> navigationViewModel.goToMainPage(Reminders)
                    is MenuTab.ITEMS -> navigationViewModel.goToFindItems(it.type)
                }
            }
        } else {
            AnimatedVisibility(navigation.mainContent != MainMenu) {
                navigation.mainContent.let {
                    when (it) {
                        is FindItems ->
                            (navigation.mainContent as FindItems).let {
                                FilteredItemListPage(
                                    type = it.type,
                                    householdId = it.householdId,
                                    showExpired = false
                                )
                            }


                        ManageMyStuff ->
                            FilteredItemListPage(
                                type = null,
                                householdId = state.householdId,
                                showExpired = true
                            )


                        ManageProfile -> Profile()

                        PublishStuff -> RoundedCornerCard {
                            ItemDetailsPage(null)
                        }

                        BoxManage -> BoxManagementPage()

                        is ShowItemDetails -> RoundedCornerCard {
                            ItemDetailsPage(it.itemId)
                        }

                        BackendInfo -> BackendInfoPage()

                        Reminders -> RoundedCornerCard { RemidersView() }

                        else -> OrganismUnderConstruction()
                    }
                }
            }
        }
    }
}
