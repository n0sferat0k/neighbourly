package com.neighbourly.app.a_device.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.atomic.molecule.RoundedCornerCard
import com.neighbourly.app.a_device.ui.atomic.organism.util.OrganismUnderConstruction
import com.neighbourly.app.a_device.ui.atomic.page.BackendInfoPage
import com.neighbourly.app.a_device.ui.auth.LoginOrRegister
import com.neighbourly.app.a_device.ui.atomic.page.BoxManagementPage
import com.neighbourly.app.a_device.ui.items.FilteredItemListView
import com.neighbourly.app.a_device.ui.items.ItemDetailsView
import com.neighbourly.app.a_device.ui.items.RemidersView
import com.neighbourly.app.a_device.ui.menu.FullMenuContent
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
            RoundedCornerCard {
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
        } else {
            AnimatedVisibility(navigation.mainContent != MainMenu) {
                navigation.mainContent.let {
                    when (it) {
                        is FindItems -> RoundedCornerCard {
                            (navigation.mainContent as FindItems).let {
                                FilteredItemListView(
                                    type = it.type,
                                    householdId = it.householdId,
                                    showExpired = false
                                )
                            }
                        }

                        ManageMyStuff -> RoundedCornerCard {
                            FilteredItemListView(
                                type = null,
                                householdId = state.householdId,
                                showExpired = true
                            )
                        }

                        ManageProfile -> RoundedCornerCard { Profile() }

                        PublishStuff -> RoundedCornerCard {
                            ItemDetailsView(null)
                        }

                        BoxManage -> BoxManagementPage()

                        is ShowItemDetails -> RoundedCornerCard {
                            ItemDetailsView(it.itemId)
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
