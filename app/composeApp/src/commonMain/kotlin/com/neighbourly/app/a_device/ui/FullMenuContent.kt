package com.neighbourly.app.a_device.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.a_device.ui.menu.LeftMenuItemBox
import com.neighbourly.app.a_device.ui.menu.RightMenuItemBox
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindBarters
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindDonations
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindEvents
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindNeeds
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindRequests
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindSales
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.FindSkillshare
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.ManageMyStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel.MainContent.PublishStuff
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.barterings
import neighbourly.composeapp.generated.resources.donate
import neighbourly.composeapp.generated.resources.donations
import neighbourly.composeapp.generated.resources.event
import neighbourly.composeapp.generated.resources.events
import neighbourly.composeapp.generated.resources.layers
import neighbourly.composeapp.generated.resources.mystuff
import neighbourly.composeapp.generated.resources.need
import neighbourly.composeapp.generated.resources.needs
import neighbourly.composeapp.generated.resources.onboard
import neighbourly.composeapp.generated.resources.profile
import neighbourly.composeapp.generated.resources.publish
import neighbourly.composeapp.generated.resources.request
import neighbourly.composeapp.generated.resources.requests
import neighbourly.composeapp.generated.resources.sale
import neighbourly.composeapp.generated.resources.sales
import neighbourly.composeapp.generated.resources.skillshare
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FullMenuContent(navigationViewModel: NavigationViewModel = viewModel { KoinProvider.KOIN.get<NavigationViewModel>() }) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxHeight().weight(.5f),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            LeftMenuItemBox(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.profile),
                image = painterResource(Res.drawable.onboard),
                delayMs = 100
            ) {
                navigationViewModel.goToProfile()
            }

            LeftMenuItemBox(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.mystuff),
                image = painterResource(Res.drawable.layers),
                delayMs = 200
            ) {
                navigationViewModel.goToMainPage(ManageMyStuff)
            }

            LeftMenuItemBox(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.publish),
                image = painterResource(Res.drawable.publish),
                delayMs = 300
            ) {
                navigationViewModel.goToMainPage(PublishStuff)
            }
        }

        Column(
            modifier = Modifier.fillMaxHeight().weight(.5f),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.donations),
                image = painterResource(Res.drawable.donate),
                delayMs = 150
            ) {
                navigationViewModel.goToMainPage(FindDonations)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.barterings),
                image = painterResource(Res.drawable.barter),
                delayMs = 250
            ) {
                navigationViewModel.goToMainPage(FindBarters)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.sales),
                image = painterResource(Res.drawable.sale),
                delayMs = 350
            ) {
                navigationViewModel.goToMainPage(FindSales)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.events),
                image = painterResource(Res.drawable.event),
                delayMs = 450
            ) {
                navigationViewModel.goToMainPage(FindEvents)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.needs),
                image = painterResource(Res.drawable.need),
                delayMs = 550
            ) {
                navigationViewModel.goToMainPage(FindNeeds)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.requests),
                image = painterResource(Res.drawable.request),
                delayMs = 650
            ) {
                navigationViewModel.goToMainPage(FindRequests)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.skillshare),
                image = painterResource(Res.drawable.skillshare),
                delayMs = 750
            ) {
                navigationViewModel.goToMainPage(FindSkillshare)
            }
        }
    }
}
