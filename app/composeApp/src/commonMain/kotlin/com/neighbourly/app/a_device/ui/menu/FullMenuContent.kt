package com.neighbourly.app.a_device.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neighbourly.app.KoinProvider
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.BoxManage
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.ManageMyStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.PublishStuff
import com.neighbourly.app.b_adapt.viewmodel.navigation.MainContent.Reminders
import com.neighbourly.app.b_adapt.viewmodel.navigation.NavigationViewModel
import com.neighbourly.app.d_entity.data.ItemType
import neighbourly.composeapp.generated.resources.Res
import neighbourly.composeapp.generated.resources.barter
import neighbourly.composeapp.generated.resources.barterings
import neighbourly.composeapp.generated.resources.box
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
import neighbourly.composeapp.generated.resources.reminder
import neighbourly.composeapp.generated.resources.reminders
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

            LeftMenuItemBox(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.reminders),
                image = painterResource(Res.drawable.reminder),
                delayMs = 500
            ) {
                navigationViewModel.goToMainPage(Reminders)
            }

            LeftMenuItemBox(
                modifier = Modifier.align(Alignment.Start),
                text = stringResource(Res.string.box),
                image = painterResource(Res.drawable.box),
                delayMs = 400
            ) {
                navigationViewModel.goToMainPage(BoxManage)
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
                navigationViewModel.goToFindItems(ItemType.DONATION)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.barterings),
                image = painterResource(Res.drawable.barter),
                delayMs = 250
            ) {
                navigationViewModel.goToFindItems(ItemType.BARTER)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.sales),
                image = painterResource(Res.drawable.sale),
                delayMs = 350
            ) {
                navigationViewModel.goToFindItems(ItemType.SALE)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.events),
                image = painterResource(Res.drawable.event),
                delayMs = 450
            ) {
                navigationViewModel.goToFindItems(ItemType.EVENT)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.needs),
                image = painterResource(Res.drawable.need),
                delayMs = 550
            ) {
                navigationViewModel.goToFindItems(ItemType.NEED)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.requests),
                image = painterResource(Res.drawable.request),
                delayMs = 650
            ) {
                navigationViewModel.goToFindItems(ItemType.REQUEST)
            }
            RightMenuItemBox(
                modifier = Modifier.align(Alignment.End),
                text = stringResource(Res.string.skillshare),
                image = painterResource(Res.drawable.skillshare),
                delayMs = 750
            ) {
                navigationViewModel.goToFindItems(ItemType.SKILLSHARE)
            }
        }
    }
}
